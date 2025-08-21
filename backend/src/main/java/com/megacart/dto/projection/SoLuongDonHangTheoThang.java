package com.megacart.dto.projection;

/**
 * Interface Projection để hứng kết quả từ query thống kê số lượng đơn hàng theo tháng.
 */
public interface SoLuongDonHangTheoThang {
    Integer getNam();
    Integer getThang();
    Long getSoLuong();
}
