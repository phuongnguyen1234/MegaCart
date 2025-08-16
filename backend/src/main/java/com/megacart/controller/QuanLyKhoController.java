package com.megacart.controller;

import com.megacart.dto.request.CapNhatKhoRequest;
import com.megacart.dto.response.ChiTietKhoResponse;
import com.megacart.dto.response.KhoResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.service.QuanLyKhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quan-ly/kho")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Chỉ Admin mới có quyền quản lý kho
public class QuanLyKhoController {

    private final QuanLyKhoService quanLyKhoService;

    @GetMapping
    public ResponseEntity<PagedResponse<KhoResponse>> getDanhSachKho(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer maDanhMuc,
            @PageableDefault(size = 30, sort = "maSanPham") Pageable pageable) {
        return ResponseEntity.ok(quanLyKhoService.getDanhSachKho(tuKhoa, maDanhMuc, pageable));
    }

    @GetMapping("/{maSanPham}")
    public ResponseEntity<ChiTietKhoResponse> getChiTietKho(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(quanLyKhoService.getChiTietKho(maSanPham));
    }

    @PatchMapping("/{maSanPham}")
    public ResponseEntity<KhoResponse> capNhatKho(
            @PathVariable Integer maSanPham,
            @Valid @RequestBody CapNhatKhoRequest request) {
        return ResponseEntity.ok(quanLyKhoService.capNhatKho(maSanPham, request));
    }
}