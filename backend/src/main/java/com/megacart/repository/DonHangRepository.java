package com.megacart.repository;

import com.megacart.dto.projection.DoanhThuTheoNgay;
import com.megacart.dto.projection.DoanhThuTheoThang;
import com.megacart.dto.projection.SoLuongDonHangTheoNgay;
import com.megacart.dto.projection.SoLuongDonHangTheoThang;
import com.megacart.dto.projection.SoLuongDonHangTheoTrangThai;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.DonHang;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    
    // Lấy đơn hàng theo mã và mã nhân viên giao hàng, fetch sẵn các chi tiết cần thiết
    @EntityGraph(attributePaths = {"chiTietDonHangs.sanPham.kho"})
    Optional<DonHang> findByMaDonHangAndNhanVienGiaoHang_MaNhanVien(Integer maDonHang, Integer maNhanVien);

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

    /**
     * Tính tổng doanh thu từ các đơn hàng đã giao thành công trong một khoảng thời gian.
     * Doanh thu được tính dựa trên tổng giá trị của các chi tiết đơn hàng.
     * Sử dụng COALESCE để trả về 0 nếu không có đơn hàng nào khớp.
     */
    @Query("SELECT COALESCE(SUM(ctdh.donGia * ctdh.soLuong), 0) FROM DonHang dh JOIN dh.chiTietDonHangs ctdh WHERE dh.trangThai = :trangThai AND dh.thoiGianDatHang BETWEEN :startTime AND :endTime")
    long sumRevenueByStatusAndTimeRange(@Param("trangThai") TrangThaiDonHang trangThai, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Đếm số lượng đơn hàng được tạo trong một khoảng thời gian.
     */
    long countByThoiGianDatHangBetween(LocalDateTime startTime, LocalDateTime endTime);

    /** Đếm số lượng đơn hàng theo một trạng thái cụ thể. */
    long countByTrangThai(TrangThaiDonHang trangThai);

    /**
     * Thống kê tổng doanh thu theo từng ngày trong một khoảng thời gian cho các đơn hàng đã giao.
     * Sử dụng FUNCTION để gọi các hàm xử lý ngày tháng của CSDL một cách linh hoạt.
     * Ví dụ: DATE() cho MySQL/SQLite, CAST(.. AS DATE) cho PostgreSQL.
     */
    @Query("SELECT FUNCTION('DATE', dh.thoiGianDatHang) as ngay, SUM(ctdh.donGia * ctdh.soLuong) as tongDoanhThu " +
           "FROM DonHang dh JOIN dh.chiTietDonHangs ctdh " +
           "WHERE dh.trangThai = :trangThai AND dh.thoiGianDatHang BETWEEN :startTime AND :endTime " +
           "GROUP BY ngay ORDER BY ngay ASC")
    List<DoanhThuTheoNgay> findDoanhThuTheoNgay(@Param("trangThai") TrangThaiDonHang trangThai, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Thống kê số lượng đơn hàng theo từng tháng trong một khoảng thời gian.
     */
    @Query("SELECT YEAR(dh.thoiGianDatHang) as nam, MONTH(dh.thoiGianDatHang) as thang, COUNT(dh.maDonHang) as soLuong " +
           "FROM DonHang dh " +
           "WHERE dh.thoiGianDatHang BETWEEN :startTime AND :endTime " +
           "GROUP BY YEAR(dh.thoiGianDatHang), MONTH(dh.thoiGianDatHang)")
    List<SoLuongDonHangTheoThang> findSoLuongDonHangTheoThangInRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);


    /**
     * Thống kê số lượng đơn hàng theo từng trạng thái.
     */
    @Query("SELECT dh.trangThai as trangThai, COUNT(dh.maDonHang) as soLuong FROM DonHang dh GROUP BY dh.trangThai")
    List<SoLuongDonHangTheoTrangThai> findSoLuongTheoTrangThai();

    /**
     * Thống kê tổng doanh thu theo từng tháng trong một khoảng thời gian cho các đơn hàng đã giao.
     */
    @Query("SELECT YEAR(dh.thoiGianDatHang) as nam, MONTH(dh.thoiGianDatHang) as thang, SUM(ctdh.donGia * ctdh.soLuong) as tongDoanhThu " +
           "FROM DonHang dh JOIN dh.chiTietDonHangs ctdh " +
          "WHERE dh.trangThai = :trangThai AND dh.thoiGianDatHang BETWEEN :startTime AND :endTime " +
           "GROUP BY YEAR(dh.thoiGianDatHang), MONTH(dh.thoiGianDatHang)")
    List<DoanhThuTheoThang> findDoanhThuTheoThangInRange(@Param("trangThai") TrangThaiDonHang trangThai, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Thống kê số lượng đơn hàng theo từng ngày trong một khoảng thời gian.
     */
    @Query("SELECT FUNCTION('DATE', dh.thoiGianDatHang) as ngay, COUNT(dh.maDonHang) as soLuong " +
           "FROM DonHang dh " +
           "WHERE dh.thoiGianDatHang BETWEEN :startTime AND :endTime " +
           "GROUP BY ngay ORDER BY ngay ASC")
    List<SoLuongDonHangTheoNgay> findSoLuongDonHangTheoNgay(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT dh.trangThai as trangThai, COUNT(dh.maDonHang) as soLuong " +
           "FROM DonHang dh " +
           "WHERE dh.thoiGianDatHang BETWEEN :startTime AND :endTime " +
           "GROUP BY dh.trangThai")
    List<SoLuongDonHangTheoTrangThai> findSoLuongTheoTrangThaiInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Ghi đè phương thức findAll để luôn fetch khách hàng, tránh N+1 query.
     */
    @Override
    @EntityGraph(attributePaths = {"khachHang"})
    Page<DonHang> findAll(Specification<DonHang> spec, Pageable pageable);
}
