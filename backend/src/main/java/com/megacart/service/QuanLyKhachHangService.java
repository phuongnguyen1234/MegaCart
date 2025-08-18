package com.megacart.service;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.response.HienThiDanhSachKhachHangResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import org.springframework.data.domain.Pageable;

public interface QuanLyKhachHangService {
    PagedResponse<HienThiDanhSachKhachHangResponse> getDSKhachHang(String searchField, String searchValue, TrangThaiTaiKhoan trangThai, Pageable pageable);

    HienThiDanhSachKhachHangResponse getChiTietKhachHang(Integer maKhachHang);

    HienThiDanhSachKhachHangResponse capNhatTrangThai(Integer maKhachHang, CapNhatTrangThaiTaiKhoanRequest request);
}