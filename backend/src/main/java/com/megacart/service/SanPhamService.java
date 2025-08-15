package com.megacart.service;

import com.megacart.dto.response.ChiTietSanPhamResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamResponse;
import com.megacart.enumeration.NhanSanPham;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SanPhamService {

    List<String> goiYTimKiem(String tuKhoa);

    PagedResponse<SanPhamResponse> timKiemVaLocSanPham(
            String tuKhoa,
            Integer maDanhMuc,
            Integer giaToiThieu,
            Integer giaToiDa,
            String nhaSanXuat,
            NhanSanPham nhan,
            Boolean banChay,
            Pageable pageable
    );

    ChiTietSanPhamResponse getSanPhamByMaSanPham(Integer maSanPham);
}