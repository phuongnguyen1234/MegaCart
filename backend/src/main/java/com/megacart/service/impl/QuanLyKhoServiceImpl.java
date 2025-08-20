package com.megacart.service.impl;

import com.megacart.dto.response.KhoResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.model.DanhMuc;
import com.megacart.model.Kho;
import com.megacart.model.SanPham;
import com.megacart.repository.KhoRepository;
import com.megacart.service.DanhMucService;
import com.megacart.service.QuanLyKhoService;
import com.megacart.dto.request.CapNhatKhoRequest;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.dto.response.ChiTietKhoResponse;
import com.megacart.enumeration.HinhThucCapNhatKho;
import com.megacart.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.JoinType;
import com.megacart.utils.ImageUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuanLyKhoServiceImpl implements QuanLyKhoService {

    private final KhoRepository khoRepository;
    private final DanhMucService danhMucService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<KhoResponse> getDanhSachKho(String searchField, String searchValue, Integer maDanhMuc, Pageable pageable) {
        Specification<Kho> spec = (root, query, cb) -> {
            // Để tránh N+1, fetch sẵn các entity liên quan.
            // Từ Kho -> SanPham -> DanhMuc -> DanhMucCha
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                var sanPhamFetch = root.fetch("sanPham", JoinType.LEFT);
                sanPhamFetch
                        .fetch("danhMuc", JoinType.LEFT)
                        .fetch("danhMucCha", JoinType.LEFT);
                sanPhamFetch.fetch("anhMinhHoas", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo trường và giá trị cụ thể
            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                Predicate searchPredicate = switch (searchField) {
                    case "maSanPham" -> searchValue.matches("\\d+")
                            ? cb.equal(root.get("maSanPham"), Integer.parseInt(searchValue))
                            : cb.disjunction(); // Luôn false nếu searchValue không phải là số
                    case "tenSanPham" -> cb.like(cb.lower(root.get("sanPham").get("tenSanPham")), "%" + searchValue.toLowerCase() + "%");
                    default -> cb.disjunction(); // Predicate luôn false nếu searchField không hợp lệ
                };
                predicates.add(searchPredicate);
            }

            // Lọc theo danh mục (bao gồm cả danh mục con)
            if (maDanhMuc != null) {
                List<Integer> categoryIds = danhMucService.getAllSubCategoryIds(maDanhMuc);
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    predicates.add(root.get("sanPham").get("danhMuc").get("maDanhMuc").in(categoryIds));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Kho> khoPage = khoRepository.findAll(spec, pageable);

        List<KhoResponse> responses = khoPage.getContent().stream()
                .map(this::mapToKhoResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, khoPage.getNumber(), khoPage.getSize(), khoPage.getTotalElements(), khoPage.getTotalPages(), null);
    }

    private KhoResponse mapToKhoResponse(Kho kho) {
        SanPham sanPham = kho.getSanPham();
        DanhMuc danhMucCon = sanPham.getDanhMuc();
        DanhMuc danhMucCha = (danhMucCon != null) ? danhMucCon.getDanhMucCha() : null;

        // Lấy URL ảnh chính để hiển thị
        String anhChinhUrl = ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas());

        return KhoResponse.builder()
                .maSanPham(kho.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .danhMucCha(danhMucCha != null ? danhMucCha.getTenDanhMuc() : null)
                .danhMucCon(danhMucCon != null ? danhMucCon.getTenDanhMuc() : null)
                .anhMinhHoaChinh(anhChinhUrl)
                .soLuong(kho.getSoLuong())
                .noiDungCapNhat(kho.getNoiDungCapNhat())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietKhoResponse getChiTietKho(Integer maSanPham) {
        Kho kho = khoRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho cho sản phẩm với mã: " + maSanPham));

        return ChiTietKhoResponse.builder()
                .maSanPham(kho.getMaSanPham())
                .tenSanPham(kho.getSanPham().getTenSanPham())
                .soLuongHienTai(kho.getSoLuong())
                .build();
    }

    @Override
    @Transactional
    public KhoResponse capNhatKho(Integer maSanPham, CapNhatKhoRequest request) {
        // Sử dụng phương thức mới để khóa hàng dữ liệu trong suốt transaction
        Kho kho = khoRepository.findByIdWithPessimisticLock(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho cho sản phẩm với mã: " + maSanPham));

        // Trường hợp ngoại lệ 1: Không cho phép cập nhật kho của sản phẩm đã ngừng kinh doanh.
        // Điều này giúp tránh việc nhập hàng cho một sản phẩm sẽ không bao giờ được bán.
        if (kho.getSanPham().getTrangThai() == TrangThaiSanPham.KHONG_BAN) {
            throw new IllegalArgumentException("Không thể cập nhật kho cho sản phẩm đã ngừng kinh doanh.");
        }

        int soLuongMoi;

        if (request.getHinhThuc() == HinhThucCapNhatKho.GHI_DE) {
            // Khi ghi đè, số lượng nhập vào không được phép là số âm.
            if (request.getSoLuong() < 0) {
                throw new IllegalArgumentException("Số lượng để ghi đè không được là số âm.");
            }
            soLuongMoi = request.getSoLuong();
        } else { // HinhThucCapNhatKho.THEM_VAO_HIEN_TAI
            soLuongMoi = kho.getSoLuong() + request.getSoLuong();
        }

        // Đảm bảo số lượng tồn kho cuối cùng không bao giờ là số âm.
        if (soLuongMoi < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không thể là số âm.");
        }

        kho.setSoLuong(soLuongMoi);
        kho.setNoiDungCapNhat(request.getNoiDung());

        
        Kho savedKho = khoRepository.save(kho);
        KhoResponse response = mapToKhoResponse(savedKho);
        response.setThongBao("Cập nhật kho thành công.");
        return response;

    }
}