package com.megacart.service.impl;

import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.ThemVaoGioHangResponse;
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
import java.util.Optional;

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
    public ThemVaoGioHangResponse themVaoGioHang(ThemVaoGioHangRequest request, TaiKhoan taiKhoan) {
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

        // 3. Tìm sản phẩm trong giỏ hàng đã được tải sẵn (nhờ @EntityGraph)
        //    Điều này tránh được lỗi NonUniqueObjectException bằng cách không query lại DB
        Optional<ChiTietGioHang> existingItemOpt = gioHang.getChiTietGioHangs().stream()
                .filter(item -> item.getSanPham().getMaSanPham().equals(request.getMaSanPham()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Nếu sản phẩm đã có, chỉ cần cập nhật số lượng trên đối tượng đã có
            ChiTietGioHang existingItem = existingItemOpt.get();
            existingItem.setSoLuong(existingItem.getSoLuong() + request.getSoLuong());
        } else {
            // Nếu sản phẩm chưa có, tạo mới và thêm vào collection của giỏ hàng
            ChiTietGioHang newItem = ChiTietGioHang.builder()
                    .id(new ChiTietGioHangId(sanPham.getMaSanPham(), gioHang.getMaKhachHang()))
                    .gioHang(gioHang)
                    .sanPham(sanPham)
                    .soLuong(request.getSoLuong())
                    .build();
            // Do có CascadeType.ALL, Hibernate sẽ tự động lưu newItem khi transaction commit
            gioHang.getChiTietGioHangs().add(newItem);
        }

        // 4. Tính toán các thông số cần thiết cho response
        int tongSoLuongSanPham = gioHang.getChiTietGioHangs().size();
        int tongSoLuongDonVi = gioHang.getChiTietGioHangs().stream()
                .mapToInt(ChiTietGioHang::getSoLuong)
                .sum();

        return ThemVaoGioHangResponse.builder()
                .message("Sản phẩm đã được thêm vào giỏ hàng thành công.")
                .tongSoLuongSanPham(tongSoLuongSanPham)
                .tongSoLuongDonVi(tongSoLuongDonVi)
                .build();
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