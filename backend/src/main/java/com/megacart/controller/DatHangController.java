package com.megacart.controller;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.model.TaiKhoan;
import com.megacart.service.DatHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dat-hang") // Sử dụng tên tài nguyên "orders" theo chuẩn REST
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('KHACH_HANG')")
public class DatHangController {

    private final DatHangService datHangService;

    @PostMapping
    public ResponseEntity<DatHangResponse> datHang(
            @Valid @RequestBody DatHangRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        DatHangResponse response = datHangService.datHang(request, taiKhoan);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
    }