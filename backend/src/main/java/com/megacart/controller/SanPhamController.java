package com.megacart.controller;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.dto.response.ChiTietSanPhamResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamResponse;
import com.megacart.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping("/goi-y")
    public ResponseEntity<List<String>> goiYTimKiem(@RequestParam("tuKhoa") String tuKhoa) {
        return ResponseEntity.ok(sanPhamService.goiYTimKiem(tuKhoa));
    }

    @GetMapping("/tim-kiem")
    public ResponseEntity<PagedResponse<SanPhamResponse>> timKiemVaLocSanPham(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer maDanhMuc,
            @RequestParam(required = false) Integer giaToiDa,
            @RequestParam(required = false) String nhaSanXuat,
            // Sắp xếp mặc định theo đơn giá tăng dần, có thể thay đổi ở frontend
            @PageableDefault(size = 20, sort = "donGia") Pageable pageable
    ) {
        return ResponseEntity.ok(sanPhamService.timKiemVaLocSanPham(tuKhoa, maDanhMuc, giaToiDa, nhaSanXuat, pageable));
    }

    @GetMapping("/theo-danh-muc/{maDanhMuc}")
    public ResponseEntity<PagedResponse<SanPhamResponse>> getSanPhamTheoDanhMuc(
            @PathVariable Integer maDanhMuc,
            @PageableDefault(size = 20, sort = "tenSanPham") Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.getSanPhamTheoDanhMuc(maDanhMuc, pageable));
    }

    @GetMapping("/theo-nhan")
    public ResponseEntity<PagedResponse<SanPhamResponse>> getSanPhamTheoNhan(
            @RequestParam("nhan") NhanSanPham nhan,
            @PageableDefault(size = 20, sort = "tenSanPham") Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.getSanPhamTheoNhan(nhan, pageable));
    }

    @GetMapping("/ban-chay")
    public ResponseEntity<PagedResponse<SanPhamResponse>> getSanPhamBanChay(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(sanPhamService.getSanPhamBanChay(pageable));
    }

    @GetMapping("/{maSanPham}")
    public ResponseEntity<ChiTietSanPhamResponse> getChiTietSanPham(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(sanPhamService.getChiTietSanPham(maSanPham));
    }
}