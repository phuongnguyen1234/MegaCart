package com.megacart.service;

public interface ThongKeSchedulerService {
    /**
     * Tác vụ được lập lịch để chạy hàng ngày, đảm bảo các bản ghi thống kê
     * cho ngày và tháng hiện tại tồn tại trong CSDL.
     */
    void ensureThongKeRecordsExist();

    /**
     * Tác vụ được lập lịch để chạy hàng ngày, tổng hợp dữ liệu của ngày hôm qua và cập nhật vào các bảng thống kê.
     */
    void aggregateDailyStatistics();

    /**
     * Tác vụ được lập lịch để chạy hàng ngày, cập nhật lại nhãn "Bán chạy" cho các sản phẩm.
     */
    void updateBestSellingProducts();
}