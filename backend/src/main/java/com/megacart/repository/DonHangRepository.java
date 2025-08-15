package com.megacart.repository;

import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.DonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    @EntityGraph(attributePaths = {"chiTietDonHangs", "chiTietDonHangs.sanPham"})
    Optional<DonHang> findByMaDonHangAndKhachHang_MaKhachHang(Integer maDonHang, Integer maKhachHang);

    /**
     * Tìm kiếm và lọc đơn hàng theo nhiều tiêu chí.
     * Các tham số lọc (tuKhoa, tuNgay, denNgay) là tùy chọn.
     * JPQL sẽ xử lý các trường hợp tham số là null.
     */
    @Query("SELECT d FROM DonHang d WHERE d.khachHang.maKhachHang = :maKhachHang AND d.trangThai = :trangThai " +
           "AND (:tuKhoa IS NULL OR CAST(d.maDonHang AS string) LIKE %:tuKhoa% OR EXISTS (SELECT 1 FROM ChiTietDonHang ctdh WHERE ctdh.donHang = d AND ctdh.tenSanPham LIKE %:tuKhoa%)) " +
           "AND (:tuNgay IS NULL OR d.thoiGianDatHang >= :tuNgay) " +
           "AND (:denNgay IS NULL OR d.thoiGianDatHang <= :denNgay)")
    @EntityGraph(attributePaths = {"chiTietDonHangs", "chiTietDonHangs.sanPham"}) // Tối ưu query để lấy chi tiết đơn hàng
    Page<DonHang> findByMaKhachHangAndTrangThaiDonHang(
            @Param("maKhachHang") Integer maKhachHang,
            @Param("trangThai") TrangThaiDonHang trangThai,
            @Param("tuKhoa") String tuKhoa,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable);
}
