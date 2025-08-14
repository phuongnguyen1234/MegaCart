package com.megacart.service;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.response.HienThiDanhSachKhachHangResponse;
import com.megacart.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface QuanLyKhachHangService {
    /**
     * Lấy danh sách khách hàng đang hoạt động cho trang quản trị.
     * @param pageable Thông tin phân trang và sắp xếp.
     * @return Một trang chứa thông tin các khách hàng.
     */
    PagedResponse<HienThiDanhSachKhachHangResponse> getDanhSachKhachHang(String searchField, String searchValue, boolean hienThiTaiKhoanBiKhoa, Pageable pageable);

    /**
     * Lấy thông tin chi tiết của một khách hàng để admin xem/sửa.
     * @param maKhachHang ID của khách hàng.
     * @return Thông tin chi tiết của khách hàng.
     */
    HienThiDanhSachKhachHangResponse getChiTietKhachHang(Integer maKhachHang);

    /**
     * Cập nhật trạng thái (hoạt động/vô hiệu hóa) cho một tài khoản khách hàng.
     * @param maKhachHang ID của khách hàng cần cập nhật.
     * @param request DTO chứa trạng thái mới.
     */
    void capNhatTrangThai(Integer maKhachHang, CapNhatTrangThaiTaiKhoanRequest request);
}