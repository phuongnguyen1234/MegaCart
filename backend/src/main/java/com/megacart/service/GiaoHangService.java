package com.megacart.service;

import com.megacart.dto.request.CapNhatGiaoHangRequest;
import com.megacart.dto.response.ChiTietDonHangGiaoHangResponse;
import com.megacart.dto.response.DonHangGiaoHangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import com.megacart.model.TaiKhoan;

public interface GiaoHangService {
    PagedResponse<DonHangGiaoHangResponse> getDSDonHangDangGiao(TaiKhoan taiKhoan, String searchField, String searchValue, Pageable pageable);

    ChiTietDonHangGiaoHangResponse getChiTietDonHang(Integer maDonHang, TaiKhoan taiKhoan);

    MessageResponse capNhatTrangThaiGiaoHang(Integer maDonHang, CapNhatGiaoHangRequest request, TaiKhoan taiKhoan);
}
