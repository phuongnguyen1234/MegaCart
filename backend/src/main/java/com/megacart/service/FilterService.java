package com.megacart.service;

import com.megacart.dto.response.FilterDataResponse;
import com.megacart.enumeration.NhanSanPham;

public interface FilterService {
    /**
     * Lấy dữ liệu cho các bộ lọc dựa trên ngữ cảnh (ví dụ: theo danh mục).
     * @param danhMucSlug Slug của danh mục (ưu tiên cao hơn tuKhoa).
     * @param tuKhoa Từ khóa tìm kiếm (sử dụng khi không có danhMucSlug).
     * @param nhan Nhãn sản phẩm (ví dụ: MOI).
     * @param banChay Cờ để xác định ngữ cảnh là trang sản phẩm bán chạy.
     * @return Một đối tượng chứa danh sách các tùy chọn cho bộ lọc.
     */
    FilterDataResponse getFilterData(String danhMucSlug, String tuKhoa, NhanSanPham nhan, boolean banChay);
}