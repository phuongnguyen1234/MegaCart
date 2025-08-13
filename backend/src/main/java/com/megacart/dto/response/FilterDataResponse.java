package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FilterDataResponse {
    private List<String> nhaSanXuats;
    private List<DanhMucMenuItemResponse> danhMucs;
    private PriceRangeResponse khoangGia;
    // Trong tương lai có thể thêm các bộ lọc khác ở đây, ví dụ: màu sắc...
}