package com.megacart.controller;

import com.megacart.dto.request.ThemSanPhamRequest;
import com.megacart.dto.request.CapNhatSanPhamRequest;
import com.megacart.dto.response.ChiTietSanPhamQuanLyResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.ThemSanPhamAsyncResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamQuanLyResponse;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.service.QuanLySanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/san-pham")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class QuanLySanPhamController {

    private final QuanLySanPhamService quanLySanPhamService;

    @GetMapping
    public ResponseEntity<PagedResponse<SanPhamQuanLyResponse>> getDSSanPham(
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) Integer maDanhMuc,
            @RequestParam(required = false) TrangThaiSanPham trangThai,
            @PageableDefault(size = 30, sort = "maSanPham", direction = Sort.Direction.ASC) Pageable pageable // Sắp xếp theo sản phẩm mới nhất
    ) {
        PagedResponse<SanPhamQuanLyResponse> response = quanLySanPhamService.getDSSanPham(searchField, searchValue, maDanhMuc, trangThai, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ThemSanPhamAsyncResponse> themSanPham(
            @Valid @RequestPart("sanPham") ThemSanPhamRequest request,
            @RequestPart("files") List<MultipartFile> files
    ) {
        ThemSanPhamAsyncResponse response = quanLySanPhamService.themSanPham(request, files);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{maSanPham}")
    public ResponseEntity<ChiTietSanPhamQuanLyResponse> getChiTietSanPham(@PathVariable Integer maSanPham) {
        ChiTietSanPhamQuanLyResponse response = quanLySanPhamService.getChiTietSanPham(maSanPham);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{maSanPham}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MessageResponse> capNhatSanPham(
            @PathVariable Integer maSanPham,
            @Valid @RequestPart("sanPham") CapNhatSanPhamRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        MessageResponse response = quanLySanPhamService.capNhatSanPham(maSanPham, request, files);
        return ResponseEntity.ok(response);
    }
}