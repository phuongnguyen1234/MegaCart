package com.megacart.service.impl;

import com.megacart.repository.MaXacThucRepository;
import com.megacart.service.ScheduledTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    private final MaXacThucRepository maXacThucRepository;

    /**
     * Tác vụ này sẽ tự động chạy vào lúc 3 giờ sáng mỗi ngày.
     * Cron expression: (giây phút giờ ngày tháng ngày-trong-tuần)
     * "0 0 3 * * ?" = 0 giây, 0 phút, 3 giờ, mỗi ngày, mỗi tháng, bất kỳ ngày nào trong tuần.
     */
    @Override
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void xoaOtpDaHetHan() {
        log.info("Bắt đầu tác vụ dọn dẹp các mã OTP đã hết hạn...");
        long deletedCount = maXacThucRepository.deleteByThoiGianHetHanBefore(Instant.now());
        log.info("Đã hoàn tất tác vụ dọn dẹp. Số lượng mã đã xóa: {}", deletedCount);
    }
}