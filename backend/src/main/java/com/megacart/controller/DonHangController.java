package com.megacart.controller;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.model.TaiKhoan;
import com.megacart.service.DonHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/don-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('KHACH_HANG')")
public class DonHangController {

    private final DonHangService donHangService;

    @PostMapping("/dat-hang")
    public ResponseEntity<DatHangResponse> datHang(
            @Valid @RequestBody DatHangRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        DatHangResponse response = donHangService.datHang(request, taiKhoan);
        return ResponseEntity.ok(response);
    }
}