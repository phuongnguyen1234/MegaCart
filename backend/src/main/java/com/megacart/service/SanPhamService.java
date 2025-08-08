package com.megacart.service;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.dto.response.ChiTietSanPhamResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SanPhamService {
    /**
     * Lấy danh sách tên sản phẩm gợi ý cho thanh tìm kiếm.
     */
    List<String> goiYTimKiem(String tuKhoa);

    /**
     * Tìm kiếm sản phẩm theo từ khóa và trả về kết quả phân trang.
     */
    PagedResponse<SanPhamResponse> timKiemVaLocSanPham(
            String tuKhoa,
            Integer maDanhMuc,
            Integer giaToiDa,
            String nhaSanXuat,
            Pageable pageable
    );

    /**
     * Lấy danh sách sản phẩm theo danh mục.
     * @param maDanhMuc ID của danh mục.
     * @param pageable Thông tin phân trang.
     * @return Danh sách sản phẩm được phân trang.
     */
    PagedResponse<SanPhamResponse> getSanPhamTheoDanhMuc(Integer maDanhMuc, Pageable pageable);

    /**
     * Lấy danh sách sản phẩm theo nhãn (brand).
     * @param nhan Tên nhãn.
     * @param pageable Thông tin phân trang.
     * @return Danh sách sản phẩm được phân trang.
     */
    PagedResponse<SanPhamResponse> getSanPhamTheoNhan(NhanSanPham nhan, Pageable pageable);

    /**
     * Lấy danh sách sản phẩm bán chạy.
     * @param pageable Thông tin phân trang.
     * @return Danh sách sản phẩm được phân trang.
     */
    PagedResponse<SanPhamResponse> getSanPhamBanChay(Pageable pageable);

    /**
     * Lấy thông tin chi tiết của một sản phẩm.
     * @param maSanPham ID của sản phẩm.
     * @return Thông tin chi tiết sản phẩm.
     */
    ChiTietSanPhamResponse getChiTietSanPham(Integer maSanPham);
}