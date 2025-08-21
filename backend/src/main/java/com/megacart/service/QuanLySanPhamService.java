package com.megacart.service;

import com.megacart.dto.request.CapNhatSanPhamRequest;
import com.megacart.dto.request.ThemSanPhamRequest;
import com.megacart.dto.response.ChiTietSanPhamQuanLyResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.ThemSanPhamAsyncResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamQuanLyResponse;
import com.megacart.enumeration.TrangThaiSanPham;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuanLySanPhamService {
    PagedResponse<SanPhamQuanLyResponse> getDSSanPham(
        String searchField,
        String searchValue,
        Integer maDanhMuc,
        TrangThaiSanPham trangThai,
        Pageable pageable
    );

    ThemSanPhamAsyncResponse themSanPham(ThemSanPhamRequest request, List<MultipartFile> files);

    ChiTietSanPhamQuanLyResponse getChiTietSanPham(Integer maSanPham);

    MessageResponse capNhatSanPham(Integer maSanPham, CapNhatSanPhamRequest request, List<MultipartFile> files);
}