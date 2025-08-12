package com.megacart.controller;

import com.megacart.dto.request.HuyDonHangRequest;
import com.megacart.dto.response.ChiTietDonHangResponse;
import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.dto.response.LichSuDonHangResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.TaiKhoan;
import com.megacart.service.DonHangService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/don-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('KHACH_HANG')")
public class DonHangController {

    private final DonHangService donHangService;

    @GetMapping("/lich-su")
    public ResponseEntity<PagedResponse<LichSuDonHangResponse>> getLichSuMuaHang(
            @AuthenticationPrincipal TaiKhoan taiKhoan,
            @RequestParam("trangThai") TrangThaiDonHang trangThai,
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @PageableDefault(size = 10, sort = "thoiGianDatHang", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResponse<LichSuDonHangResponse> response = donHangService.getLichSuMuaHang(taiKhoan, trangThai, tuKhoa, tuNgay, denNgay, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{maDonHang}")
    public ResponseEntity<ChiTietDonHangResponse> getChiTietDonHang(
            @PathVariable Integer maDonHang,
            @AuthenticationPrincipal TaiKhoan taiKhoan
    ) {
        ChiTietDonHangResponse response = donHangService.getChiTietDonHang(maDonHang, taiKhoan);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{maDonHang}/huy")
    public ResponseEntity<ChiTietDonHangResponse> huyDonHang(
            @PathVariable Integer maDonHang,
            @Valid @RequestBody HuyDonHangRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan
    ) {
        ChiTietDonHangResponse response = donHangService.huyDonHang(maDonHang, request, taiKhoan);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{maDonHang}/giao-phan-con-lai")
    public ResponseEntity<ChiTietDonHangResponse> giaoPhanConLai(
            @PathVariable Integer maDonHang,
            @AuthenticationPrincipal TaiKhoan taiKhoan
    ) {
        ChiTietDonHangResponse response = donHangService.giaoPhanConLai(maDonHang, taiKhoan);
        return ResponseEntity.ok(response);
    }
}