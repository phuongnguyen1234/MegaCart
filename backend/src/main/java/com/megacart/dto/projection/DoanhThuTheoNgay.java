package com.megacart.dto.projection;

import java.time.LocalDate;

/**
 * Interface Projection để hứng kết quả từ query thống kê doanh thu theo ngày.
 */
public interface DoanhThuTheoNgay {
    LocalDate getNgay();
    Long getTongDoanhThu();
}
