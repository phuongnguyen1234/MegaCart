package com.megacart.repository;

import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.DonHang;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DonHangRepository extends JpaRepository<DonHang, Integer>, JpaSpecificationExecutor<DonHang> {

    /**
     * Ghi đè phương thức findById để luôn fetch các quan hệ cần thiết cho trang chi tiết.
     * @param maDonHang ID của đơn hàng.
     * @return Optional chứa đơn hàng nếu tìm thấy.
     */
    @Override
    @EntityGraph(attributePaths = {"khachHang", "chiTietDonHangs.sanPham.anhMinhHoas", "chiTietDonHangs.sanPham.kho"})
    Optional<DonHang> findById(Integer maDonHang);

    @EntityGraph(attributePaths = {"chiTietDonHangs", "chiTietDonHangs.sanPham"})
    Optional<DonHang> findByMaDonHangAndKhachHang_MaKhachHang(Integer maDonHang, Integer maKhachHang);
    
    boolean existsByKhachHang_MaKhachHangAndTrangThaiIn(Integer maKhachHang, List<TrangThaiDonHang> trangThais);

    /**
     * Tìm kiếm và lọc đơn hàng theo nhiều tiêu chí.
     * Các tham số lọc (tuKhoa, tuNgay, denNgay) là tùy chọn.
     * JPQL sẽ xử lý các trường hợp tham số là null.
     */
    @Query("SELECT d FROM DonHang d WHERE d.khachHang.maKhachHang = :maKhachHang AND d.trangThai = :trangThai " +
           "AND (:tuKhoa IS NULL OR CAST(d.maDonHang AS string) LIKE %:tuKhoa% OR EXISTS (SELECT 1 FROM ChiTietDonHang ctdh WHERE ctdh.donHang = d AND ctdh.tenSanPham LIKE %:tuKhoa%)) " +
           "AND (:tuNgay IS NULL OR d.thoiGianDatHang >= :tuNgay) " +
           "AND (:denNgay IS NULL OR d.thoiGianDatHang <= :denNgay)")
    Page<DonHang> findByMaKhachHangAndTrangThaiDonHang(
            @Param("maKhachHang") Integer maKhachHang,
            @Param("trangThai") TrangThaiDonHang trangThai,
            @Param("tuKhoa") String tuKhoa,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable);
}
