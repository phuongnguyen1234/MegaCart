package com.megacart.controller;

import com.megacart.dto.response.MessageResponse;
import com.megacart.service.ThongKeSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/debug")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
// Kích hoạt controller này chỉ khi profile là 'dev'.
@Profile("dev")
public class AdminDebugController {

    private final ThongKeSchedulerService thongKeSchedulerService;

    @PostMapping("/trigger-daily-stats")
    public ResponseEntity<MessageResponse> triggerDailyStats() {
        thongKeSchedulerService.aggregateDailyStatistics();
        return ResponseEntity.ok(new MessageResponse("Đã kích hoạt thành công tác vụ tổng hợp thống kê ngày."));
    }

    @PostMapping("/trigger-bestsellers")
    public ResponseEntity<MessageResponse> triggerBestsellersUpdate() {
        thongKeSchedulerService.updateBestSellingProducts();
        return ResponseEntity.ok(new MessageResponse("Đã kích hoạt thành công tác vụ cập nhật sản phẩm bán chạy."));
    }
}