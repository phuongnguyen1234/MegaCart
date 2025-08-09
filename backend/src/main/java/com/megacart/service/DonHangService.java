package com.megacart.service;

import com.megacart.dto.request.HuyDonHangRequest;
import com.megacart.dto.response.ChiTietDonHangResponse;
import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.dto.response.LichSuDonHangResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.TaiKhoan;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface DonHangService {

    DatHangResponse datHang(DatHangRequest request, TaiKhoan taiKhoan);

    PagedResponse<LichSuDonHangResponse> getLichSuMuaHang(TaiKhoan taiKhoan, TrangThaiDonHang trangThai, String tuKhoa, LocalDate tuNgay, LocalDate denNgay, Pageable pageable);

    ChiTietDonHangResponse getChiTietDonHang(Integer maDonHang, TaiKhoan taiKhoan);

    ChiTietDonHangResponse huyDonHang(Integer maDonHang, HuyDonHangRequest request, TaiKhoan taiKhoan);

    ChiTietDonHangResponse giaoPhanConLai(Integer maDonHang, TaiKhoan taiKhoan);

}
