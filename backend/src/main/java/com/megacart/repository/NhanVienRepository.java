package com.megacart.repository;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.model.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer>, JpaSpecificationExecutor<NhanVien> {
    /**
     * Tìm kiếm nhân viên dựa trên trạng thái của tài khoản liên kết.
     */
    @Query("SELECT nv FROM NhanVien nv JOIN nv.taiKhoan tk WHERE tk.trangThaiTaiKhoan = :trangThai")
    Page<NhanVien> findByTrangThaiTaiKhoan(@Param("trangThai") TrangThaiTaiKhoan trangThai, Pageable pageable);

    @Query("SELECT nv FROM NhanVien nv JOIN FETCH nv.taiKhoan WHERE nv.maNhanVien = :maNhanVien")
    Optional<NhanVien> findByIdWithTaiKhoan(@Param("maNhanVien") Integer maNhanVien);
}