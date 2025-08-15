package com.megacart.controller;

import com.megacart.dto.request.CapNhatDanhMucRequest;
import com.megacart.dto.response.ChiTietDanhMucQuanLyResponse;
import com.megacart.dto.request.ThemDanhMucRequest;
import com.megacart.dto.response.DanhMucQuanLyResponse;
import com.megacart.dto.response.DanhMucOptionResponse;
import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.dto.response.PagedResponse;
import com.megacart.service.DanhMucService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/danh-muc")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class QuanLyDanhMucController {

    private final DanhMucService danhMucService;

    // Đổi tên endpoint để rõ ràng hơn về mục đích
    @GetMapping("/filter-options")
    public ResponseEntity<List<DanhMucOptionResponse>> getDanhMucOptionsForFilter() {
        return ResponseEntity.ok(danhMucService.getDanhMucOptionsForFilter());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<DanhMucQuanLyResponse>> getDanhSachDanhMuc(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) TrangThaiDanhMuc trangThai,
            @PageableDefault(size = 30, sort = "maDanhMuc", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PagedResponse<DanhMucQuanLyResponse> response = danhMucService.getDanhSachDanhMuc(tuKhoa, trangThai, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DanhMucQuanLyResponse> themDanhMuc(@Valid @RequestBody ThemDanhMucRequest request) {
        DanhMucQuanLyResponse response = danhMucService.themDanhMuc(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{maDanhMuc}")
    public ResponseEntity<ChiTietDanhMucQuanLyResponse> getChiTietDanhMuc(@PathVariable Integer maDanhMuc) {
        ChiTietDanhMucQuanLyResponse response = danhMucService.getChiTietDanhMuc(maDanhMuc);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{maDanhMuc}")
    public ResponseEntity<DanhMucQuanLyResponse> capNhatDanhMuc(
            @PathVariable Integer maDanhMuc,
            @Valid @RequestBody CapNhatDanhMucRequest request) {
        DanhMucQuanLyResponse response = danhMucService.capNhatDanhMuc(maDanhMuc, request);
        return ResponseEntity.ok(response);
    }
}