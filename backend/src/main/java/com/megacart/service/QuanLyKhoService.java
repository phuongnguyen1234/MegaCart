package com.megacart.service;

import com.megacart.dto.response.KhoResponse;
import com.megacart.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface QuanLyKhoService {

    PagedResponse<KhoResponse> getDanhSachKho(String tuKhoa, Integer maDanhMuc, Pageable pageable);

}