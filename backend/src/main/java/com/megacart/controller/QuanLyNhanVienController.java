package com.megacart.controller;

import com.megacart.dto.request.CapNhatNhanVienRequest;
import com.megacart.dto.request.ThemNhanVienRequest;
import com.megacart.dto.response.HienThiDanhSachNhanVienResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.ViTri;
import com.megacart.service.QuanLyNhanVienService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/nhan-vien")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class QuanLyNhanVienController {

    private final QuanLyNhanVienService quanLyNhanVienService;

    @GetMapping
    public ResponseEntity<PagedResponse<HienThiDanhSachNhanVienResponse>> getDanhSachNhanVien(
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) ViTri viTri,
            @RequestParam(defaultValue = "false") boolean hienThiTaiKhoanBiKhoa,
            @PageableDefault(size = 30, sort = "hoTen", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(quanLyNhanVienService.getDanhSachNhanVien(searchField, searchValue, viTri, hienThiTaiKhoanBiKhoa, pageable));
    }

    /**
     * Tạo một tài khoản nhân viên mới.
     * @param request DTO chứa thông tin nhân viên mới.
     * @return Thông tin chi tiết của nhân viên vừa được tạo.
     */
    @PostMapping
    public ResponseEntity<HienThiDanhSachNhanVienResponse> themNhanVien(@Valid @RequestBody ThemNhanVienRequest request) {
        HienThiDanhSachNhanVienResponse response = quanLyNhanVienService.themNhanVien(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Lấy thông tin chi tiết của một nhân viên để hiển thị trên form chỉnh sửa.
     * @param maNhanVien ID của nhân viên.
     * @return Thông tin chi tiết của nhân viên.
     */
    @GetMapping("/{maNhanVien}")
    public ResponseEntity<HienThiDanhSachNhanVienResponse> getChiTietNhanVien(@PathVariable Integer maNhanVien) {
        return ResponseEntity.ok(quanLyNhanVienService.getChiTietNhanVien(maNhanVien));
    }

    /**
     * Cập nhật thông tin của một nhân viên.
     * @param maNhanVien ID của nhân viên.
     * @param request DTO chứa các thông tin cần thay đổi.
     * @return Thông tin chi tiết của nhân viên sau khi cập nhật.
     */
    @PatchMapping("/{maNhanVien}")
    public ResponseEntity<HienThiDanhSachNhanVienResponse> capNhatNhanVien(
            @PathVariable Integer maNhanVien,
            @Valid @RequestBody CapNhatNhanVienRequest request) {
        HienThiDanhSachNhanVienResponse response = quanLyNhanVienService.capNhatNhanVien(maNhanVien, request);
        return ResponseEntity.ok(response);
    }
}
