package com.megacart.service.impl;

import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.GioHangResponse;
import com.megacart.dto.response.ThongTinKhachHangResponse;
import com.megacart.dto.response.ThongTinThanhToanResponse;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import com.megacart.utils.ImageUtils;
import com.megacart.model.*;
import com.megacart.repository.ChiTietGioHangRepository;
import com.megacart.repository.GioHangRepository;
import com.megacart.repository.SanPhamRepository;
import com.megacart.service.GioHangService;
import com.megacart.service.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GioHangServiceImpl implements GioHangService {

    private final SanPhamRepository sanPhamRepository;
    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final KhachHangService khachHangService;

    @Override
    @Transactional
    public GioHangResponse themVaoGioHang(ThemVaoGioHangRequest request, TaiKhoan taiKhoan) {
        // 1. Tìm sản phẩm và kiểm tra tồn kho
        SanPham sanPham = sanPhamRepository.findById(request.getMaSanPham())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + request.getMaSanPham()));

        if (sanPham.getKho() == null || sanPham.getKho().getSoLuong() < request.getSoLuong()) {
            throw new IllegalArgumentException("Sản phẩm đã hết hàng hoặc không đủ số lượng yêu cầu.");
        }

        // 2. Lấy giỏ hàng của khách hàng (hoặc tạo mới nếu chưa có)
        GioHang gioHang = gioHangRepository.findByKhachHang_MaKhachHang(taiKhoan.getMaTaiKhoan())
                .orElseGet(() -> {
                    GioHang newCart = new GioHang();
                    newCart.setKhachHang(taiKhoan.getKhachHang());
                    return gioHangRepository.save(newCart);
                });

        // 3. Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        ChiTietGioHangId chiTietId = new ChiTietGioHangId(sanPham.getMaSanPham(), gioHang.getMaKhachHang());
        ChiTietGioHang chiTietGioHang = chiTietGioHangRepository.findById(chiTietId)
                .map(item -> {
                    // Nếu đã có, cập nhật số lượng
                    item.setSoLuong(item.getSoLuong() + request.getSoLuong());
                    return item;
                })
                .orElseGet(() -> {
                    // Nếu chưa có, tạo mới
                    return ChiTietGioHang.builder()
                            .id(chiTietId)
                            .gioHang(gioHang)
                            .sanPham(sanPham)
                            .soLuong(request.getSoLuong())
                            .build();
                });

        // 4. Lưu lại chi tiết giỏ hàng
        chiTietGioHangRepository.save(chiTietGioHang);

        // 5. Trả về thông tin giỏ hàng đã cập nhật
        return buildGioHangResponse(gioHang);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongTinThanhToanResponse getThongTinThanhToan(TaiKhoan taiKhoan) {
        // 1. Lấy thông tin giỏ hàng (đã được tối ưu bằng @EntityGraph)
        GioHangResponse gioHangResponse = gioHangRepository.findByKhachHang_MaKhachHang(taiKhoan.getMaTaiKhoan())
                .map(this::buildGioHangResponse)
                .orElse(GioHangResponse.builder() // Nếu không có giỏ hàng, trả về giỏ hàng rỗng
                        .items(Collections.emptyList())
                        .tongSoLuongSanPham(0)
                        .tongTien(0)
                        .build());

        // 2. Lấy thông tin giao hàng mặc định của khách hàng
        // Tái sử dụng KhachHangService để tránh lặp lại logic và tuân thủ nguyên tắc đơn trách nhiệm
        ThongTinKhachHangResponse thongTinGiaoHangMacDinh = khachHangService.getThongTinKhachHang(taiKhoan.getMaTaiKhoan());

        // 3. Lấy các tùy chọn thanh toán và giao hàng từ Enum
        List<HinhThucNhanHang> hinhThucNhanHangOptions = Arrays.asList(HinhThucNhanHang.values());
        List<HinhThucThanhToan> hinhThucThanhToanOptions = Arrays.asList(HinhThucThanhToan.values());

        // 4. Xây dựng và trả về response cuối cùng
        return ThongTinThanhToanResponse.builder()
                .items(gioHangResponse.getItems())
                .tongSoLuongSanPham(gioHangResponse.getTongSoLuongSanPham())
                .tongTien(gioHangResponse.getTongTien())
                .thongTinGiaoHangMacDinh(thongTinGiaoHangMacDinh)
                .hinhThucNhanHangOptions(hinhThucNhanHangOptions)
                .hinhThucThanhToanOptions(hinhThucThanhToanOptions)
                .build();
    }

    private GioHangResponse buildGioHangResponse(GioHang gioHang) {
        // Không cần tải lại giỏ hàng vì @EntityGraph đã làm việc đó
        List<GioHangResponse.ChiTietGioHangItem> items = gioHang.getChiTietGioHangs().stream()
                .map(item -> {
                    long thanhTien = (long) item.getSanPham().getDonGia() * item.getSoLuong();
                    return GioHangResponse.ChiTietGioHangItem.builder()
                            .maSanPham(item.getSanPham().getMaSanPham())
                            .tenSanPham(item.getSanPham().getTenSanPham())
                            .anhMinhHoa(ImageUtils.getAnhMinhHoaChinhUrl(item.getSanPham().getAnhMinhHoas()))
                            .donGia(item.getSanPham().getDonGia())
                            .donVi(item.getSanPham().getDonVi())
                            .soLuong(item.getSoLuong())
                            .thanhTien(thanhTien)
                            .build();
                }).collect(Collectors.toList());
        
        long tongTien = items.stream().mapToLong(GioHangResponse.ChiTietGioHangItem::getThanhTien).sum();

        return GioHangResponse.builder().items(items).tongSoLuongSanPham(items.size()).tongTien(tongTien).build();
    }
}