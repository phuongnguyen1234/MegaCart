package com.megacart.repository;

import com.megacart.model.ThongKeNgay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ThongKeNgayRepository extends JpaRepository<ThongKeNgay, Integer> {
    // Tìm kiếm bản ghi thống kê theo ngày
    Optional<ThongKeNgay> findByNgay(LocalDate ngay);
}