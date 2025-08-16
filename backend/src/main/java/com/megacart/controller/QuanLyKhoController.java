package com.megacart.controller;

import com.megacart.dto.response.KhoResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.service.QuanLyKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quan-ly/kho")
@RequiredArgsConstructor
public class QuanLyKhoController {

    private final QuanLyKhoService quanLyKhoService;

    @GetMapping
    public ResponseEntity<PagedResponse<KhoResponse>> getDanhSachKho(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer maDanhMuc,
            @PageableDefault(size = 30, sort = "maSanPham") Pageable pageable) {
        return ResponseEntity.ok(quanLyKhoService.getDanhSachKho(tuKhoa, maDanhMuc, pageable));
    }
}