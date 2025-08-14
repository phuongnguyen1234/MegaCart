package com.megacart.controller;

import com.megacart.dto.response.DanhMucMenuItemResponse;
import com.megacart.service.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/danh-muc")
@RequiredArgsConstructor
public class DanhMucController {

    private final DanhMucService danhMucService;

    @GetMapping("/menu")
    public ResponseEntity<List<DanhMucMenuItemResponse>> getMenuDanhMucs() {
        return ResponseEntity.ok(danhMucService.getMenuDanhMucs());
    }
}