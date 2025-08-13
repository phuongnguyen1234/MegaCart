package com.megacart.repository;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.model.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer>, JpaSpecificationExecutor<KhachHang> {

    @Query("SELECT kh FROM KhachHang kh JOIN FETCH kh.taiKhoan WHERE kh.maKhachHang = :maKhachHang")
    Optional<KhachHang> findByIdWithTaiKhoan(Integer maKhachHang);

    /**
     * Tìm kiếm khách hàng dựa trên trạng thái của tài khoản liên kết.
     */
    @Query("SELECT kh FROM KhachHang kh JOIN kh.taiKhoan tk " +
           "WHERE tk.trangThaiTaiKhoan = :trangThai")
    Page<KhachHang> findByTrangThaiTaiKhoan(@Param("trangThai") TrangThaiTaiKhoan trangThai, Pageable pageable);
}