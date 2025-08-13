package com.megacart.service;

import com.megacart.dto.response.FilterDataResponse;

public interface FilterService {
    /**
     * Lấy dữ liệu cho các bộ lọc dựa trên ngữ cảnh (ví dụ: theo danh mục).
     * @param danhMucSlug Slug của danh mục (ưu tiên cao hơn tuKhoa).
     * @param tuKhoa Từ khóa tìm kiếm (sử dụng khi không có danhMucSlug).
     * @return Một đối tượng chứa danh sách các tùy chọn cho bộ lọc.
     */
    FilterDataResponse getFilterData(String danhMucSlug, String tuKhoa);
}