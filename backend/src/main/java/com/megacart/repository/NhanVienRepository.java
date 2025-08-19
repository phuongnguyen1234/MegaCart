package com.megacart.repository;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.enumeration.ViTri;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer>, JpaSpecificationExecutor<NhanVien> {
    /**
     * Tìm kiếm nhân viên dựa trên trạng thái của tài khoản liên kết.
     */
    @Query("SELECT nv FROM NhanVien nv JOIN nv.taiKhoan tk WHERE tk.trangThaiTaiKhoan = :trangThai") Page<NhanVien> findByTrangThaiTaiKhoan(@Param("trangThai") TrangThaiTaiKhoan trangThai, Pageable pageable);

    @Query("SELECT nv FROM NhanVien nv JOIN FETCH nv.taiKhoan WHERE nv.maNhanVien = :maNhanVien") Optional<NhanVien> findByIdWithTaiKhoan(@Param("maNhanVien") Integer maNhanVien);

    // Lấy danh sách nhân viên theo vị trí và trạng thái tài khoản
    List<NhanVien> findByViTriAndTaiKhoan_TrangThaiTaiKhoan(ViTri viTri, TrangThaiTaiKhoan trangThai);

    /**
     * Tìm nhân viên giao hàng đang hoạt động và có ít đơn hàng đang giao nhất.
     * LEFT JOIN để bao gồm cả những nhân viên chưa có đơn hàng nào.
     * @param pageable Chỉ cần lấy 1 người (PageRequest.of(0, 1)).
     * @return Danh sách (chứa 1 hoặc 0) nhân viên phù hợp.
     */
    @Query("SELECT nv FROM NhanVien nv " +
           "LEFT JOIN DonHang dh ON dh.nhanVienGiaoHang = nv AND dh.trangThai = :trangThaiDonHang " +
           "WHERE nv.viTri = :viTri AND nv.taiKhoan.trangThaiTaiKhoan = :trangThaiTaiKhoan " +
           "GROUP BY nv ORDER BY COUNT(dh) ASC")
    List<NhanVien> findNhanVienGiaoHangItViecNhat(
            @Param("viTri") ViTri viTri, @Param("trangThaiTaiKhoan") TrangThaiTaiKhoan trangThaiTaiKhoan, @Param("trangThaiDonHang") TrangThaiDonHang trangThaiDonHang, Pageable pageable);
}