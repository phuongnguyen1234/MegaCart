package com.megacart.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ThoiGianGiaoHangUtils {

    private static final LocalTime GIO_CAT_DON = LocalTime.of(21, 0); // 9h tối
    private static final LocalTime GIO_GIAO_HOM_SAU = LocalTime.of(7, 0); // 7h sáng
    private static final int SO_GIO_GIAO_TRONG_NGAY = 2; // Giao hàng trong vòng 2 tiếng

    private ThoiGianGiaoHangUtils() {
        // Private constructor để ngăn việc tạo instance của utility class
    }

    public static LocalDateTime tinhThoiGianGiaoHangDuKien(LocalDateTime thoiGianDatHang) {
        if (thoiGianDatHang.toLocalTime().isAfter(GIO_CAT_DON)) {
            // Nếu đặt sau 9h tối, giao vào 7h sáng hôm sau
            LocalDate ngayHomSau = thoiGianDatHang.toLocalDate().plusDays(1);
            return LocalDateTime.of(ngayHomSau, GIO_GIAO_HOM_SAU);
        } else {
            // Nếu đặt trong ngày, giao sau X tiếng
            return thoiGianDatHang.plusHours(SO_GIO_GIAO_TRONG_NGAY);
        }
    }
}