package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * DTO chứa dữ liệu cho các thẻ thống kê tổng quan trên dashboard.
 * Cấu trúc được thiết kế để khớp với yêu cầu của component frontend.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Không hiển thị các trường có giá trị null trong JSON
public class ThongKeTongQuanResponse {
    private DoanhThu doanhThu;
    private DonHang donHang;

    @Data
    @Builder
    public static class DoanhThu {
        private long homNay;
        private long thangNay;
        private Double tangTruongSoVoiThangTruoc; // Dùng Double để có số thập phân, có thể là null
    }

    @Data
    @Builder
    public static class DonHang {
        private long homNay;
        private long thangNay;
        private Double tangTruongSoVoiThangTruoc;
        private long soDonDangGiao;
    }
}
