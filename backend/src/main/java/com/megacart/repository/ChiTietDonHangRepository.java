package com.megacart.repository;

import com.megacart.dto.projection.SoLuongDaBanProjection;
import com.megacart.dto.projection.TongTienDonHangProjection;
import com.megacart.dto.projection.ChiTietSanPhamBanChayProjection;
import com.megacart.dto.projection.SanPhamBanChayProjection;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.ChiTietDonHang;
import com.megacart.model.ChiTietDonHangId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, ChiTietDonHangId> {
    // Lấy tất cả chi tiết đơn hàng cho một danh sách các mã đơn hàng
    // Dùng EntityGraph để fetch sẵn sản phẩm, tránh N+1 query khi truy cập sanPhamEntity
    @EntityGraph(attributePaths = {"sanPham"})
    List<ChiTietDonHang> findByDonHang_MaDonHangIn(List<Integer> donHangIds);

    @Query("SELECT ctdh.sanPham.tenSanPham as tenSanPham, SUM(ctdh.soLuong) as soLuongDaBan " +
           "FROM ChiTietDonHang ctdh WHERE ctdh.donHang.trangThai = :trangThai " +
           "GROUP BY ctdh.sanPham.maSanPham, ctdh.sanPham.tenSanPham ORDER BY soLuongDaBan DESC")
    List<SanPhamBanChayProjection> findTopBanChay(@Param("trangThai") TrangThaiDonHang trangThai, Pageable pageable);

    @Query("SELECT ctdh.sanPham.maSanPham as maSanPham, SUM(ctdh.soLuong) as soLuongDaBan " +
           "FROM ChiTietDonHang ctdh " +
           "WHERE ctdh.donHang.trangThai = :trangThai AND ctdh.sanPham.maSanPham IN :maSanPhams " +
           "GROUP BY ctdh.sanPham.maSanPham")
    List<SoLuongDaBanProjection> findSoLuongDaBanBySanPhamIds(@Param("trangThai") TrangThaiDonHang trangThai, @Param("maSanPhams") List<Integer> maSanPhams);

    @Query("SELECT " +
           "ctdh.sanPham.maSanPham as maSanPham, " +
           "ctdh.sanPham.tenSanPham as tenSanPham, " +
           "SUM(ctdh.soLuong) as soLuongBanRa, " +
           "COUNT(DISTINCT ctdh.donHang.maDonHang) as soDonDat " +
           "FROM ChiTietDonHang ctdh WHERE ctdh.donHang.trangThai = :trangThai " +
           "GROUP BY ctdh.sanPham.maSanPham, ctdh.sanPham.tenSanPham ORDER BY soLuongBanRa DESC")
    Page<ChiTietSanPhamBanChayProjection> findChiTietBanChay(@Param("trangThai") TrangThaiDonHang trangThai, Pageable pageable);

    @Query("SELECT COALESCE(SUM(ctdh.soLuong), 0) " +
           "FROM ChiTietDonHang ctdh " +
           "WHERE ctdh.donHang.thoiGianDatHang BETWEEN :startTime AND :endTime")
    long sumSoLuongByDonHangThoiGianDatHangBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT ctdh.donHang.maDonHang as maDonHang, SUM(ctdh.donGia * ctdh.soLuong) as tongTien " +
           "FROM ChiTietDonHang ctdh " +
           "WHERE ctdh.donHang.maDonHang IN :donHangIds " +
           "GROUP BY ctdh.donHang.maDonHang")
    List<TongTienDonHangProjection> findTongTienByDonHangIds(@Param("donHangIds") List<Integer> donHangIds);
}