package com.megacart.service.impl;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.enumeration.TrangThaiTonKho;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.dto.response.AnhMinhHoaResponse;
import com.megacart.dto.response.ChiTietSanPhamResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamResponse;
import com.megacart.model.AnhMinhHoa;
import com.megacart.model.SanPham;
import com.megacart.repository.SanPhamRepository;
import com.megacart.repository.specification.SanPhamSpecification;
import com.megacart.service.SanPhamService;
import com.megacart.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamSpecification sanPhamSpecification;
    private static final int GOI_Y_LIMIT = 10; // Giới hạn 10 gợi ý

    @Override
    @Transactional(readOnly = true)
    public List<String> goiYTimKiem(String tuKhoa) {
        Pageable limit = PageRequest.of(0, GOI_Y_LIMIT);
        // Chuẩn bị pattern tìm kiếm ngay tại tầng service
        String searchPattern = tuKhoa + "%";
        return sanPhamRepository.findTenSanPhamByPrefixAndStatus(searchPattern, TrangThaiSanPham.DANG_BAN, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> timKiemVaLocSanPham(
            String tuKhoa,
            Integer maDanhMuc,
            Integer giaToiDa,
            String nhaSanXuat,
            Pageable pageable
    ) {
        Specification<SanPham> spec = sanPhamSpecification.filterBy(tuKhoa, maDanhMuc, giaToiDa, nhaSanXuat);
        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);
        return convertPageToPagedResponse(sanPhamPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> getSanPhamTheoDanhMuc(Integer maDanhMuc, Pageable pageable) {
        Page<SanPham> sanPhamPage = sanPhamRepository.findByDanhMuc_MaDanhMucAndTrangThai(maDanhMuc, TrangThaiSanPham.DANG_BAN, pageable);
        return convertPageToPagedResponse(sanPhamPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> getSanPhamTheoNhan(NhanSanPham nhan, Pageable pageable) {
        Page<SanPham> sanPhamPage = sanPhamRepository.findByNhanAndTrangThai(nhan, TrangThaiSanPham.DANG_BAN, pageable);
        return convertPageToPagedResponse(sanPhamPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SanPhamResponse> getSanPhamBanChay(Pageable pageable) {
        // Bước 1: Lấy trang ID của các sản phẩm bán chạy
        Page<Integer> maSanPhamPage = sanPhamRepository.findMaSanPhamBanChay(TrangThaiSanPham.DANG_BAN, pageable);
        List<Integer> maSanPhams = maSanPhamPage.getContent();

        if (maSanPhams.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0);
        }

        // Bước 2: Lấy thông tin chi tiết cho các ID đó
        List<SanPham> sanPhams = sanPhamRepository.findByIdsWithDetails(maSanPhams);

        // Sắp xếp lại danh sách sản phẩm theo đúng thứ tự bán chạy từ Bước 1
        Map<Integer, SanPham> sanPhamMap = sanPhams.stream()
                .collect(Collectors.toMap(SanPham::getMaSanPham, sanPham -> sanPham));

        List<SanPham> sortedSanPhams = maSanPhams.stream()
                .map(sanPhamMap::get)
                .filter(Objects::nonNull)
                .toList();

        // Chuyển đổi sang DTO và trả về PagedResponse
        List<SanPhamResponse> sanPhamResponses = sortedSanPhams.stream().map(this::mapToSanPhamResponse).toList();
        return new PagedResponse<>(sanPhamResponses, maSanPhamPage.getNumber(), maSanPhamPage.getSize(), maSanPhamPage.getTotalElements(), maSanPhamPage.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietSanPhamResponse getChiTietSanPham(Integer maSanPham) {
        SanPham sanPham = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + maSanPham));

        // Chỉ cho phép xem sản phẩm đang được bán
        if (sanPham.getTrangThai() != TrangThaiSanPham.DANG_BAN) {
            throw new ResourceNotFoundException("Sản phẩm này không tồn tại hoặc đã ngừng kinh doanh.");
        }

        return mapToChiTietSanPhamResponse(sanPham);
    }

    /**
     * Phương thức private helper để chuyển đổi từ Page<SanPham> sang PagedResponse<SanPhamResponse>.
     * Tái sử dụng logic mapping để tránh lặp code.
     * @param sanPhamPage Đối tượng Page chứa các sản phẩm.
     * @return Đối tượng PagedResponse chứa DTO sản phẩm.
     */
    private SanPhamResponse mapToSanPhamResponse(SanPham sanPham) {
        String anhChinhUrl = ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas());
        // Suy ra trạng thái tồn kho từ số lượng trong bảng Kho
        TrangThaiTonKho trangThaiTonKho = (sanPham.getKho() != null && sanPham.getKho().getSoLuong() > 0)
                ? TrangThaiTonKho.CON_HANG
                : TrangThaiTonKho.HET_HANG;

        return SanPhamResponse.builder()
                .maSanPham(sanPham.getMaSanPham()).tenSanPham(sanPham.getTenSanPham()).donGia(sanPham.getDonGia())
                .donVi(sanPham.getDonVi()).nhaSanXuat(sanPham.getNhaSanXuat()).nhan(sanPham.getNhan())
                .trangThaiTonKho(trangThaiTonKho).anhMinhHoaChinh(anhChinhUrl).build();
    }

    private PagedResponse<SanPhamResponse> convertPageToPagedResponse(Page<SanPham> sanPhamPage) {
        List<SanPhamResponse> sanPhamResponses = sanPhamPage.getContent().stream()
                .map(this::mapToSanPhamResponse)
                .toList();

        return new PagedResponse<>(sanPhamResponses,
                sanPhamPage.getNumber(),
                sanPhamPage.getSize(),
                sanPhamPage.getTotalElements(),
                sanPhamPage.getTotalPages());
    }

    private ChiTietSanPhamResponse mapToChiTietSanPhamResponse(SanPham sanPham) {
        TrangThaiTonKho trangThaiTonKho = (sanPham.getKho() != null && sanPham.getKho().getSoLuong() > 0)
                ? TrangThaiTonKho.CON_HANG
                : TrangThaiTonKho.HET_HANG;

        List<AnhMinhHoaResponse> anhMinhHoas = sanPham.getAnhMinhHoas().stream()
                .map(amh -> AnhMinhHoaResponse.builder().duongDan(amh.getDuongDan()).laAnhChinh(amh.isLaAnhChinh()).build())
                .collect(Collectors.toList());

        return ChiTietSanPhamResponse.builder()
                .maSanPham(sanPham.getMaSanPham()).tenSanPham(sanPham.getTenSanPham()).donGia(sanPham.getDonGia())
                .donVi(sanPham.getDonVi()).nhaSanXuat(sanPham.getNhaSanXuat())
                .moTa(sanPham.getMoTa()).ghiChu(sanPham.getGhiChu()).trangThaiTonKho(trangThaiTonKho)
                .anhMinhHoas(anhMinhHoas).build();
    }
}