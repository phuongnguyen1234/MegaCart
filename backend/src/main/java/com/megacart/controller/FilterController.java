package com.megacart.controller;

import com.megacart.dto.response.FilterDataResponse;
import com.megacart.enumeration.NhanSanPham;
import com.megacart.service.FilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/filter-options") // <-- ĐỔI TÊN ENDPOINT
@RequiredArgsConstructor
@Slf4j
public class FilterController {

    private final FilterService filterService;

    @GetMapping
    public ResponseEntity<FilterDataResponse> getFilters(
            @RequestParam(required = false) String danhMucSlug,
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) NhanSanPham nhan
    ) {
        log.info(">>> Request received for /api/filter-options with slug: {}, keyword: {}, label: {}", danhMucSlug, tuKhoa, nhan);
        return ResponseEntity.ok(filterService.getFilterData(danhMucSlug, tuKhoa, nhan));
    }
}