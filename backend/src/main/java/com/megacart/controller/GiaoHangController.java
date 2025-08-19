package com.megacart.controller;

import com.megacart.dto.request.CapNhatGiaoHangRequest;
import com.megacart.dto.response.ChiTietDonHangGiaoHangResponse;
import com.megacart.dto.response.DonHangGiaoHangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.service.GiaoHangService;
import com.megacart.model.TaiKhoan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/giao-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('NHAN_VIEN_GIAO_HANG')")
public class GiaoHangController {

    private final GiaoHangService giaoHangService;

    @GetMapping("/don-hang")
    public ResponseEntity<PagedResponse<DonHangGiaoHangResponse>> getDSDonHangDangGiao(
            @AuthenticationPrincipal TaiKhoan taiKhoan,
            @RequestParam(required = false) String searchField, @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 30, sort = "thoiGianDatHang", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(giaoHangService.getDSDonHangDangGiao(taiKhoan, searchField, searchValue, pageable));
    }

    @GetMapping("/don-hang/{maDonHang}")
    public ResponseEntity<ChiTietDonHangGiaoHangResponse> getChiTietDonHang(
            @PathVariable Integer maDonHang,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        return ResponseEntity.ok(giaoHangService.getChiTietDonHang(maDonHang, taiKhoan));
    }

    @PatchMapping("/don-hang/{maDonHang}")
    public ResponseEntity<MessageResponse> capNhatTrangThaiGiaoHang(
            @PathVariable Integer maDonHang,
            @Valid @RequestBody CapNhatGiaoHangRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        return ResponseEntity.ok(giaoHangService.capNhatTrangThaiGiaoHang(maDonHang, request, taiKhoan));
    }
}
