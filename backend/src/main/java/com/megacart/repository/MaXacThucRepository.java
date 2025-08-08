package com.megacart.repository;

import com.megacart.enumeration.LoaiXacThuc;
import com.megacart.model.MaXacThuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface MaXacThucRepository extends JpaRepository<MaXacThuc, Integer> {

    Optional<MaXacThuc> findByTaiKhoan_Email(String email);

    Optional<MaXacThuc> findByTaiKhoan_MaTaiKhoanAndLoai(Integer maTaiKhoan, LoaiXacThuc loai);

    Optional<MaXacThuc> findByTaiKhoan_EmailAndLoai(String email, LoaiXacThuc loai);

    // Phương thức này cần thiết cho ScheduledTaskService để dọn dẹp OTP hết hạn
    long deleteByThoiGianHetHanBefore(Instant now);
}