package com.megacart.controller;

import com.megacart.dto.request.CapNhatDonHangRequest;
import com.megacart.dto.response.ChiTietDonHangQuanLyResponse;
import com.megacart.dto.response.DonHangQuanLyResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.service.QuanLyDonHangService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/don-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('NHAN_VIEN_QUAN_LI_DON') or hasAuthority('ADMIN')")
public class QuanLyDonHangController {

    private final QuanLyDonHangService quanLyDonHangService;

    @GetMapping
    public ResponseEntity<PagedResponse<DonHangQuanLyResponse>> getDSDonHang(
            @RequestParam(required = false) String searchField, @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) TrangThaiDonHang trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayDat,
            @PageableDefault(size = 30, sort = "thoiGianDatHang", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(quanLyDonHangService.getDSDonHang(searchField, searchValue, trangThai, ngayDat, pageable));
    }
    
    @GetMapping("/{maDonHang}")
    public ResponseEntity<ChiTietDonHangQuanLyResponse> getChiTietDonHang(@PathVariable Integer maDonHang) {
        return ResponseEntity.ok(quanLyDonHangService.getChiTietDonHang(maDonHang));
    }
    
    @PatchMapping("/{maDonHang}")
    public ResponseEntity<ChiTietDonHangQuanLyResponse> capNhatDonHang(
            @PathVariable Integer maDonHang,
            @Valid @RequestBody CapNhatDonHangRequest request) {
        return ResponseEntity.ok(quanLyDonHangService.capNhatDonHang(maDonHang, request));
    }
}

