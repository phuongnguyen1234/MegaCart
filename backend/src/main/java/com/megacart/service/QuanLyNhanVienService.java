package com.megacart.service;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.request.CapNhatNhanVienRequest;
import com.megacart.dto.request.ThemNhanVienRequest;
import com.megacart.dto.response.HienThiDanhSachNhanVienResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.enumeration.ViTri;
import org.springframework.data.domain.Pageable;

public interface QuanLyNhanVienService {
    /**
     * Lấy danh sách nhân viên đang hoạt động cho trang quản trị.
     * @param pageable Thông tin phân trang và sắp xếp.
     * @return Một trang chứa thông tin các nhân viên.
     */
    PagedResponse<HienThiDanhSachNhanVienResponse> getDSNhanVien(String searchField, String searchValue, ViTri viTri, TrangThaiTaiKhoan trangThai, Pageable pageable);

    /**
     * Tạo một tài khoản nhân viên mới.
     * @param request DTO chứa thông tin nhân viên mới.
     * @return Thông tin chi tiết của nhân viên vừa được tạo.
     */
    HienThiDanhSachNhanVienResponse themNhanVien(ThemNhanVienRequest request);

    /**
     * Lấy thông tin chi tiết của một nhân viên để admin xem/sửa.
     * @param maNhanVien ID của nhân viên.
     * @return Thông tin chi tiết của nhân viên.
     */
    HienThiDanhSachNhanVienResponse getChiTietNhanVien(Integer maNhanVien);

    /**
     * Cập nhật thông tin của một nhân viên.
     * @param maNhanVien ID của nhân viên cần cập nhật.
     * @param request DTO chứa các thông tin cần thay đổi.
     * @return Thông tin chi tiết của nhân viên sau khi đã cập nhật.
     */
    HienThiDanhSachNhanVienResponse capNhatNhanVien(Integer maNhanVien, CapNhatNhanVienRequest request);
}