package com.megacart.service;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.response.HienThiDanhSachKhachHangResponse;
import com.megacart.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface QuanLyKhachHangService {
    PagedResponse<HienThiDanhSachKhachHangResponse> getDSKhachHang(String searchField, String searchValue, boolean hienThiTaiKhoanBiKhoa, Pageable pageable);

    HienThiDanhSachKhachHangResponse getChiTietKhachHang(Integer maKhachHang);

    HienThiDanhSachKhachHangResponse capNhatTrangThai(Integer maKhachHang, CapNhatTrangThaiTaiKhoanRequest request);
}