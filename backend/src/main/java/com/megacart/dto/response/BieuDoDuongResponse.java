package com.megacart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO chung cho dữ liệu biểu đồ đường, khớp với props của component frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BieuDoDuongResponse {
    private List<String> labels;
    private List<? extends Number> data; // Dùng wildcard để linh hoạt cho cả doanh thu (long) và số lượng (int)
}
