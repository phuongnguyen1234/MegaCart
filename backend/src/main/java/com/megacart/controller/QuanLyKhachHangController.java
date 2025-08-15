package com.megacart.controller;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.response.HienThiDanhSachKhachHangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.service.QuanLyKhachHangService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/khach-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Chỉ Admin mới có quyền truy cập
public class QuanLyKhachHangController {

    private final QuanLyKhachHangService quanLyKhachHangService;

    /**
     * Lấy danh sách khách hàng có phân trang, sắp xếp và tìm kiếm động.
     * @param searchField Thuộc tính cần tìm (e.g., 'tenKhachHang', 'email').
     * @param searchValue Giá trị cần tìm.
     * @param hienThiTaiKhoanBiKhoa Cờ để lọc theo trạng thái tài khoản.
     * @param pageable Spring sẽ tự động tạo đối tượng Pageable từ các tham số ?page, ?size, ?sort trên URL.
     *                 PageableDefault cung cấp các giá trị mặc định nếu frontend không gửi lên.
     * @return Một trang chứa danh sách khách hàng.
     */
    @GetMapping
    public ResponseEntity<PagedResponse<HienThiDanhSachKhachHangResponse>> getDanhSachKhachHang(
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String searchValue,
            @RequestParam(defaultValue = "false") boolean hienThiTaiKhoanBiKhoa,
            // Yêu cầu: 30 tài khoản/trang, sắp xếp theo tên tăng dần
            @PageableDefault(size = 30, sort = "tenKhachHang", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PagedResponse<HienThiDanhSachKhachHangResponse> response = quanLyKhachHangService.getDanhSachKhachHang(searchField, searchValue, hienThiTaiKhoanBiKhoa, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lấy thông tin chi tiết của một khách hàng để hiển thị trên form chỉnh sửa.
     * @param maKhachHang ID của khách hàng.
     * @return Thông tin chi tiết của khách hàng.
     */
    @GetMapping("/{maKhachHang}")
    public ResponseEntity<HienThiDanhSachKhachHangResponse> getChiTietKhachHang(@PathVariable Integer maKhachHang) {
        return ResponseEntity.ok(quanLyKhachHangService.getChiTietKhachHang(maKhachHang));
    }

    /**
     * Cập nhật trạng thái của một tài khoản khách hàng.
     * @param maKhachHang ID của khách hàng.
     * @param request DTO chứa trạng thái mới.
     */
    @PatchMapping("/{maKhachHang}/status")
    public ResponseEntity<MessageResponse> capNhatTrangThai(
            @PathVariable Integer maKhachHang,
            @Valid @RequestBody CapNhatTrangThaiTaiKhoanRequest request
    ) {
        quanLyKhachHangService.capNhatTrangThai(maKhachHang, request);
        String message = "Cập nhật trạng thái tài khoản thành công.";
        return ResponseEntity.ok(new MessageResponse(message));
    }
}