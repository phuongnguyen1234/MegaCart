package com.megacart.controller;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.response.HienThiDanhSachKhachHangResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.service.QuanLyKhachHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/khach-hang") // Đồng bộ với các controller admin khác
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class QuanLyKhachHangController {

    private final QuanLyKhachHangService quanLyKhachHangService;

    @GetMapping
    public ResponseEntity<PagedResponse<HienThiDanhSachKhachHangResponse>> getDSKhachHang(
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) TrangThaiTaiKhoan trangThai, // Nếu không truyền, sẽ lấy tất cả
            @PageableDefault(size = 30, sort = "maKhachHang") Pageable pageable) {
        return ResponseEntity.ok(quanLyKhachHangService.getDSKhachHang(searchField, searchValue, trangThai, pageable));
    }

    @GetMapping("/{maKhachHang}")
    public ResponseEntity<HienThiDanhSachKhachHangResponse> getChiTietKhachHang(@PathVariable Integer maKhachHang) {
        return ResponseEntity.ok(quanLyKhachHangService.getChiTietKhachHang(maKhachHang));
    }

    @PatchMapping("/{maKhachHang}/cap-nhat-trang-thai")
    public ResponseEntity<HienThiDanhSachKhachHangResponse> capNhatTrangThai(@PathVariable Integer maKhachHang, @Valid @RequestBody CapNhatTrangThaiTaiKhoanRequest request) {
        return ResponseEntity.ok(quanLyKhachHangService.capNhatTrangThai(maKhachHang, request));
    }
}