package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatDonHangRequest;
import com.megacart.dto.response.ChiTietDonHangQuanLyResponse;
import com.megacart.dto.response.DonHangQuanLyResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.ChiTietDonHang;
import com.megacart.model.DonHang;
import com.megacart.model.Kho;
import com.megacart.model.SanPham;
import com.megacart.repository.DonHangRepository;
import com.megacart.repository.ChiTietDonHangRepository;
import com.megacart.repository.specification.TimKiemDonHangSpecification;
import com.megacart.service.QuanLyDonHangService;
import com.megacart.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class QuanLyDonHangServiceImpl implements QuanLyDonHangService {

    private final DonHangRepository donHangRepository;
    private final TimKiemDonHangSpecification timKiemDonHangSpecification;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DonHangQuanLyResponse> getDSDonHang(String searchField, String searchValue, TrangThaiDonHang trangThai, LocalDate ngayDat, Pageable pageable) {
        // Bước 1: Lấy trang danh sách đơn hàng
        Specification<DonHang> spec = timKiemDonHangSpecification.filterBy(searchField, searchValue, trangThai, ngayDat);
        Page<DonHang> donHangPage = donHangRepository.findAll(spec, pageable);

        List<DonHang> donHangsOnPage = donHangPage.getContent();
        if (donHangsOnPage.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
        }

        // Bước 2: Lấy tất cả chi tiết đơn hàng cho các đơn hàng trên trang hiện tại trong 1 query
        List<Integer> donHangIds = donHangsOnPage.stream().map(DonHang::getMaDonHang).toList();
        List<ChiTietDonHang> allDetails = chiTietDonHangRepository.findByDonHang_MaDonHangIn(donHangIds);

        // Nhóm các chi tiết theo mã đơn hàng và tính tổng tiền
        Map<Integer, Integer> tongTienTheoDonHang = allDetails.stream()
                .collect(Collectors.groupingBy(
                        ct -> ct.getDonHang().getMaDonHang(),
                        Collectors.summingInt(ct -> ct.getDonGia() * ct.getSoLuong())
                ));

        // Bước 3: Ánh xạ sang DTO, sử dụng tổng tiền đã tính toán
        List<DonHangQuanLyResponse> responses = donHangsOnPage.stream()
                .map(donHang -> mapToDonHangQuanLyResponse(donHang, tongTienTheoDonHang.getOrDefault(donHang.getMaDonHang(), 0)))
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
    }

    private DonHangQuanLyResponse mapToDonHangQuanLyResponse(DonHang donHang, int tongTien) {
        return DonHangQuanLyResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                // Lấy tên người nhận hàng trực tiếp từ đơn hàng để nhất quán
                .tenKhachHang(donHang.getTenKhachHang())
                .thoiGianDatHang(donHang.getThoiGianDatHang() != null ? donHang.getThoiGianDatHang().atZone(ZoneId.systemDefault()).toInstant() : null)
                .trangThai(donHang.getTrangThai())
                .tongTien(tongTien)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietDonHangQuanLyResponse getChiTietDonHang(Integer maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));

        return mapToChiTietDonHangQuanLyResponse(donHang);
    }

    private ChiTietDonHangQuanLyResponse mapToChiTietDonHangQuanLyResponse(DonHang donHang) {
        List<ChiTietDonHangQuanLyResponse.Item> items = donHang.getChiTietDonHangs().stream()
                .map(chiTiet -> {
                    SanPham sanPham = chiTiet.getSanPham();
                    String anhChinhUrl = (sanPham != null) ? ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas()) : null;

                    return ChiTietDonHangQuanLyResponse.Item.builder()
                            .maSanPham(sanPham != null ? sanPham.getMaSanPham() : null)
                            .tenSanPham(chiTiet.getTenSanPham())
                            .anhMinhHoaChinh(anhChinhUrl)
                            .donGia(chiTiet.getDonGia())
                            .donVi(chiTiet.getDonVi())
                            .soLuong(chiTiet.getSoLuong())
                            .tongTien((long) chiTiet.getDonGia() * chiTiet.getSoLuong())
                            .build();
                })
                .collect(Collectors.toList());

        long tongTienDonHang = items.stream().mapToLong(ChiTietDonHangQuanLyResponse.Item::getTongTien).sum();

        return ChiTietDonHangQuanLyResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .tongTien(tongTienDonHang)
                .tenNguoiNhan(donHang.getTenKhachHang())
                .sdtNhanHang(donHang.getSdtNhanHang())
                .diaChiNhanHang(donHang.getDiaChiNhanHang())
                .thoiGianDatHang(donHang.getThoiGianDatHang() != null ? donHang.getThoiGianDatHang().atZone(ZoneId.systemDefault()).toInstant() : null)
                .hinhThucGiaoHang(donHang.getHinhThucNhanHang())
                .hinhThucThanhToan(donHang.getHinhThucThanhToan())
                .trangThai(donHang.getTrangThai())
                .trangThaiThanhToan(donHang.getTrangThaiThanhToan())
                .duKienGiaoHang(donHang.getDuKienGiaoHang())
                .ghiChu(donHang.getGhiChu())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public ChiTietDonHangQuanLyResponse capNhatDonHang(Integer maDonHang, CapNhatDonHangRequest request) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));

        // Cập nhật trạng thái đơn hàng (có ràng buộc nghiệp vụ)
        if (request.getTrangThai() != null && request.getTrangThai() != donHang.getTrangThai()) {
            xuLyThayDoiTrangThaiDonHang(donHang, request.getTrangThai());
        }

        // Cập nhật trạng thái thanh toán
        if (request.getTrangThaiThanhToan() != null && request.getTrangThaiThanhToan() != donHang.getTrangThaiThanhToan()) {
            donHang.setTrangThaiThanhToan(request.getTrangThaiThanhToan());
            // Nếu thanh toán thành công, ghi nhận thời gian
            if (request.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
                donHang.setThoiGianThanhToan(LocalDateTime.now());
            }
        }

        // Cập nhật ngày giao hàng dự kiến
        if (request.getDuKienGiaoHang() != null) {
            donHang.setDuKienGiaoHang(request.getDuKienGiaoHang());
        }

        // Cập nhật ghi chú
        if (request.getGhiChu() != null) {
            donHang.setGhiChu(request.getGhiChu());
        }

        // Lưu lại các thay đổi
        DonHang savedDonHang = donHangRepository.save(donHang);

        // Trả về response với thông tin đã cập nhật và thông báo thành công
        ChiTietDonHangQuanLyResponse response = mapToChiTietDonHangQuanLyResponse(savedDonHang);
        response.setThongBao("Cập nhật đơn hàng thành công.");
        return response;
    }

    private void xuLyThayDoiTrangThaiDonHang(DonHang donHang, TrangThaiDonHang trangThaiMoi) {
        TrangThaiDonHang trangThaiHienTai = donHang.getTrangThai();

        // Ràng buộc nghiệp vụ: Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy.
        if (trangThaiHienTai == TrangThaiDonHang.DA_GIAO || trangThaiHienTai == TrangThaiDonHang.DA_HUY) {
            throw new IllegalArgumentException("Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy.");
        }

        // Kiểm tra tính hợp lệ của việc chuyển đổi trạng thái
        boolean chuyenDoiHopLe = switch (trangThaiHienTai) {
            case CHO_XAC_NHAN -> trangThaiMoi == TrangThaiDonHang.CHO_XU_LY || trangThaiMoi == TrangThaiDonHang.DANG_GIAO || trangThaiMoi == TrangThaiDonHang.DA_HUY;
            case CHO_XU_LY -> trangThaiMoi == TrangThaiDonHang.DANG_GIAO || trangThaiMoi == TrangThaiDonHang.DA_HUY;
            case DANG_GIAO -> trangThaiMoi == TrangThaiDonHang.DA_GIAO || trangThaiMoi == TrangThaiDonHang.DA_HUY;
            default -> false;
        };

        if (!chuyenDoiHopLe) {
            throw new IllegalArgumentException("Không thể chuyển đơn hàng từ trạng thái '" + trangThaiHienTai + "' sang '" + trangThaiMoi + "'.");
        }

        // Xử lý hoàn trả hàng vào kho khi đơn hàng bị hủy
        if (trangThaiMoi == TrangThaiDonHang.DA_HUY) {
            // Chỉ hoàn kho cho các đơn hàng đã trừ kho (trạng thái đang xử lý hoặc đang giao)
            if (trangThaiHienTai == TrangThaiDonHang.CHO_XU_LY || trangThaiHienTai == TrangThaiDonHang.DANG_GIAO) {
                for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                    // Kiểm tra sản phẩm và kho có tồn tại không để tránh NullPointerException
                    if (chiTiet.getSanPham() != null && chiTiet.getSanPham().getKho() != null) {
                        Kho kho = chiTiet.getSanPham().getKho();
                        kho.setSoLuong(kho.getSoLuong() + chiTiet.getSoLuong());
                    }
                }
            }
        }

        // Ghi nhận thời gian giao hàng khi chuyển sang trạng thái "Đã giao"
        if (trangThaiMoi == TrangThaiDonHang.DA_GIAO) {
            // Nếu chưa thanh toán, tự động chuyển sang đã thanh toán khi giao hàng thành công (COD)
            if (donHang.getTrangThaiThanhToan() == TrangThaiThanhToan.CHUA_THANH_TOAN) {
                donHang.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
                donHang.setThoiGianThanhToan(LocalDateTime.now());
            }
        }

        donHang.setTrangThai(trangThaiMoi);
    }
}
