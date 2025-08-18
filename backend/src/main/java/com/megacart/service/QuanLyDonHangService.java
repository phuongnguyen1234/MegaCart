package com.megacart.service;

import com.megacart.dto.request.CapNhatDonHangRequest;
import com.megacart.dto.response.ChiTietDonHangQuanLyResponse;
import com.megacart.dto.response.DonHangQuanLyResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface QuanLyDonHangService {
    PagedResponse<DonHangQuanLyResponse> getDSDonHang(String searchField, String searchValue, TrangThaiDonHang trangThai,
                                                      LocalDate ngayDat, Pageable pageable);

    ChiTietDonHangQuanLyResponse getChiTietDonHang(Integer maDonHang);

    ChiTietDonHangQuanLyResponse capNhatDonHang(Integer maDonHang, CapNhatDonHangRequest request);
}
