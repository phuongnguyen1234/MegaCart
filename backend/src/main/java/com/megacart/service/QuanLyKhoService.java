package com.megacart.service;

import com.megacart.dto.request.CapNhatKhoRequest;
import com.megacart.dto.response.ChiTietKhoResponse;
import com.megacart.dto.response.KhoResponse;
import com.megacart.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface QuanLyKhoService {

    PagedResponse<KhoResponse> getDanhSachKho(String searchField, String searchValue, Integer maDanhMuc, Pageable pageable);

    ChiTietKhoResponse getChiTietKho(Integer maSanPham);

    KhoResponse capNhatKho(Integer maSanPham, CapNhatKhoRequest request);

}