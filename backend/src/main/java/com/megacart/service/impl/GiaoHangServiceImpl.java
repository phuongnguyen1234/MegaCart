package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatGiaoHangRequest;
import com.megacart.dto.response.ChiTietDonHangGiaoHangResponse;
import com.megacart.dto.response.DonHangGiaoHangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.KetQuaGiaoHang;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.dto.projection.TongTienDonHangProjection;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.ChiTietDonHang;
import com.megacart.model.TaiKhoan;
import com.megacart.model.DonHang;
import com.megacart.model.Kho;
import com.megacart.model.SanPham;
import com.megacart.repository.ChiTietDonHangRepository;
import com.megacart.repository.DonHangRepository;
import com.megacart.repository.specification.TimKiemDonHangSpecification;
import com.megacart.service.GiaoHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.megacart.utils.ImageUtils;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiaoHangServiceImpl implements GiaoHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TimKiemDonHangSpecification timKiemDonHangSpecification;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DonHangGiaoHangResponse> getDSDonHangDangGiao(TaiKhoan taiKhoan, String searchField, String searchValue, Pageable pageable) {
        // Bước 1: Tạo Specification với bộ lọc CỐ ĐỊNH là trạng thái "ĐANG_GIAO" và mã nhân viên đang đăng nhập
        Specification<DonHang> spec = (root, query, cb) -> {
            // Điều kiện CỐ ĐỊNH: trạng thái "ĐANG_GIAO" và đúng mã nhân viên giao hàng
            jakarta.persistence.criteria.Predicate shipperPredicate = cb.equal(root.get("nhanVienGiaoHang").get("maNhanVien"), taiKhoan.getMaTaiKhoan());
            jakarta.persistence.criteria.Predicate statusPredicate = cb.equal(root.get("trangThai"), TrangThaiDonHang.DANG_GIAO);
            
            // Điều kiện lọc động (nếu có)
            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                Specification<DonHang> searchSpec = timKiemDonHangSpecification.filterBy(searchField, searchValue, null, null);
                return cb.and(shipperPredicate, statusPredicate, searchSpec.toPredicate(root, query, cb));
            }

            return cb.and(shipperPredicate, statusPredicate);
        };

        Page<DonHang> donHangPage = donHangRepository.findAll(spec, pageable);

        List<DonHang> donHangsOnPage = donHangPage.getContent();
        if (donHangsOnPage.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
        }

        // Bước 2: Lấy chi tiết và tính tổng tiền (tương tự logic quản lý đơn hàng để đảm bảo hiệu suất)
        List<Integer> donHangIds = donHangsOnPage.stream().map(DonHang::getMaDonHang).toList();
        // Tối ưu: Sử dụng query đã được tối ưu để tính tổng tiền tại CSDL
        Map<Integer, Long> tongTienTheoDonHang = chiTietDonHangRepository.findTongTienByDonHangIds(donHangIds)
                .stream()
                .collect(Collectors.toMap(TongTienDonHangProjection::getMaDonHang, TongTienDonHangProjection::getTongTien));

        // Bước 3: Ánh xạ sang DTO
        List<DonHangGiaoHangResponse> responses = donHangsOnPage.stream()
                .map(donHang -> DonHangGiaoHangResponse.builder()
                        .maDonHang(donHang.getMaDonHang()).tenNguoiNhan(donHang.getTenKhachHang()).sdtNhanHang(donHang.getSdtNhanHang())
                        .diaChiNhanHang(donHang.getDiaChiNhanHang()).tongTien(tongTienTheoDonHang.getOrDefault(donHang.getMaDonHang(), 0L))
                        .trangThaiThanhToan(donHang.getTrangThaiThanhToan()).build())
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietDonHangGiaoHangResponse getChiTietDonHang(Integer maDonHang, TaiKhoan taiKhoan) {
        // Tìm đơn hàng theo mã và đảm bảo nó thuộc về nhân viên giao hàng đang đăng nhập
        DonHang donHang = donHangRepository.findByMaDonHangAndNhanVienGiaoHang_MaNhanVien(maDonHang, taiKhoan.getMaTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này."));

        // Ràng buộc nghiệp vụ: Nhân viên giao hàng chỉ được xem chi tiết đơn hàng đang giao.
        if (donHang.getTrangThai() != TrangThaiDonHang.DANG_GIAO) {
            throw new IllegalArgumentException("Chỉ có thể xem chi tiết đơn hàng đang ở trạng thái 'Đang giao'.");
        }

        return mapToChiTietDonHangGiaoHangResponse(donHang);
    }

    private ChiTietDonHangGiaoHangResponse mapToChiTietDonHangGiaoHangResponse(DonHang donHang) {
        List<ChiTietDonHangGiaoHangResponse.Item> items = donHang.getChiTietDonHangs().stream()
                .map(chiTiet -> {
                    SanPham sanPham = chiTiet.getSanPham();
                    String anhChinhUrl = (sanPham != null) ? ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas()) : null;

                    return ChiTietDonHangGiaoHangResponse.Item.builder()
                            .tenSanPham(chiTiet.getTenSanPham())
                            .anhMinhHoaChinh(anhChinhUrl)
                            .donGia(chiTiet.getDonGia())
                            .soLuong(chiTiet.getSoLuong())
                            .tongTienSanPham((long) chiTiet.getDonGia() * chiTiet.getSoLuong())
                            .build();
                })
                .collect(Collectors.toList());

        long tongTienDonHang = items.stream().mapToLong(ChiTietDonHangGiaoHangResponse.Item::getTongTienSanPham).sum();

        return ChiTietDonHangGiaoHangResponse.builder()
                .maDonHang(donHang.getMaDonHang()).tenNguoiNhan(donHang.getTenKhachHang()).sdtNhanHang(donHang.getSdtNhanHang())
                .diaChiNhanHang(donHang.getDiaChiNhanHang()).thoiGianDatHang(donHang.getThoiGianDatHang())
                .hinhThucGiaoHang(donHang.getHinhThucNhanHang()).hinhThucThanhToan(donHang.getHinhThucThanhToan())
                .ghiChu(donHang.getGhiChu()).tongTien(tongTienDonHang).items(items).build();
    }

    @Override
    @Transactional
    public MessageResponse capNhatTrangThaiGiaoHang(Integer maDonHang, CapNhatGiaoHangRequest request, TaiKhoan taiKhoan) {
        // Tìm đơn hàng theo mã và đảm bảo nó thuộc về nhân viên giao hàng đang đăng nhập
        DonHang donHang = donHangRepository.findByMaDonHangAndNhanVienGiaoHang_MaNhanVien(maDonHang, taiKhoan.getMaTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền thực hiện thao tác này."));

        // Ràng buộc 1: Chỉ có thể cập nhật đơn hàng đang ở trạng thái "Đang giao".
        if (donHang.getTrangThai() != TrangThaiDonHang.DANG_GIAO) {
            throw new IllegalArgumentException("Chỉ có thể cập nhật trạng thái cho đơn hàng đang giao.");
        }

        if (request.getKetQua() == KetQuaGiaoHang.THANH_CONG) {
            // Xử lý giao hàng thành công
            donHang.setTrangThai(TrangThaiDonHang.DA_GIAO);
            
            // Logic mới: Tự động xử lý thanh toán cho đơn COD khi giao thành công.
            // Điều này đảm bảo không bao giờ có trạng thái "Đã giao" mà "Chưa thanh toán".
            if (donHang.getTrangThaiThanhToan() == TrangThaiThanhToan.CHUA_THANH_TOAN) {
                // Bắt buộc shipper phải xác nhận đã thu tiền cho đơn COD.
                if (request.getTrangThaiThanhToan() != TrangThaiThanhToan.DA_THANH_TOAN) {
                    throw new IllegalArgumentException("Với đơn hàng COD, phải xác nhận 'Đã thanh toán' khi giao hàng thành công.");
                }
                donHang.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
                donHang.setThoiGianThanhToan(LocalDateTime.now());
            }
            // Đối với đơn đã thanh toán online, không làm gì cả, trạng thái thanh toán vẫn là DA_THANH_TOAN.

        } else { // KetQuaGiaoHang.THAT_BAI
            // Xử lý giao hàng thất bại (thử giao lại)
            if (request.getLyDoThatBai() == null || request.getLyDoThatBai().isBlank()) {
                throw new IllegalArgumentException("Cần cung cấp lý do khi giao hàng thất bại.");
            }

            // Logic mới: Cập nhật thời gian giao hàng dự kiến để thử lại
            LocalDateTime thoiGianHienTai = LocalDateTime.now();
            LocalDateTime thoiGianGiaoHangMoi = thoiGianHienTai.plusHours(3);

            // Nếu thời gian giao mới là từ 21h (9 PM) trở đi
            if (thoiGianGiaoHangMoi.getHour() >= 21) {
                // Chuyển sang 6h sáng ngày hôm sau
                thoiGianGiaoHangMoi = thoiGianGiaoHangMoi.toLocalDate().plusDays(1).atTime(6, 0);
            }
            donHang.setDuKienGiaoHang(thoiGianGiaoHangMoi);

            // Nối lý do thất bại vào ghi chú của đơn hàng để theo dõi
            String ghiChuCu = donHang.getGhiChu();
            String lyDoMoi = "Giao thất bại lúc " + thoiGianHienTai.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")) + ": " + request.getLyDoThatBai();
            String ghiChuMoi;
            if (StringUtils.hasText(ghiChuCu)) {
                ghiChuMoi = ghiChuCu + " | " + lyDoMoi; // Dùng dấu | để phân cách các lần ghi chú
            } else {
                ghiChuMoi = lyDoMoi;
            }
            donHang.setGhiChu(ghiChuMoi);

            // Giữ nguyên trạng thái DANG_GIAO và không hoàn kho để shipper có thể giao lại.
        }
        return new MessageResponse("Cập nhật trạng thái giao hàng thành công.");
    }
}
