package com.megacart.repository;


import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer>, JpaSpecificationExecutor<SanPham> {

    /**
     * Tìm kiếm tên sản phẩm để gợi ý (autocomplete).
     * Chỉ lấy tên sản phẩm để tối ưu performance.
     */
    @Query("SELECT sp.tenSanPham FROM SanPham sp WHERE sp.tenSanPham LIKE %:tuKhoa% AND sp.trangThai = 'DANG_BAN'")
    List<String> findTenSanPhamByTuKhoa(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    /**
     * Tìm kiếm sản phẩm đầy đủ thông tin theo từ khóa, có phân trang.
     * Chỉ tìm các sản phẩm đang ở trạng thái DANG_BAN.
     * Sử dụng EntityGraph để fetch các quan hệ cần thiết, tránh lỗi N+1 query.
     */
    @EntityGraph(attributePaths = {"kho", "anhMinhHoas"})
    Page<SanPham> findByTenSanPhamContainingIgnoreCaseAndTrangThai(String tuKhoa, TrangThaiSanPham trangThai, Pageable pageable);

    /**
     * Lấy danh sách sản phẩm theo mã danh mục, có phân trang.
     * Chỉ lấy các sản phẩm đang ở trạng thái DANG_BAN.
     */
    @EntityGraph(attributePaths = {"kho", "anhMinhHoas"})
    Page<SanPham> findByDanhMuc_MaDanhMucAndTrangThai(Integer maDanhMuc, TrangThaiSanPham trangThai, Pageable pageable);

    /**
     * Lấy danh sách sản phẩm theo nhãn, có phân trang.
     * Chỉ lấy các sản phẩm đang ở trạng thái DANG_BAN.
     */
    @EntityGraph(attributePaths = {"kho", "anhMinhHoas"})
    Page<SanPham> findByNhanAndTrangThai(NhanSanPham nhan, TrangThaiSanPham trangThai, Pageable pageable);

    /**
     * Bước 1: Lấy danh sách ID của các sản phẩm bán chạy nhất, có phân trang.
     * - Chỉ tính các sản phẩm trong các đơn hàng đã giao thành công (DA_GIAO).
     * - Sử dụng LEFT JOIN để bao gồm cả các sản phẩm chưa bán được.
     * - Sắp xếp dựa trên tổng số lượng đã bán.
     */
    @Query(value = "SELECT sp.maSanPham FROM SanPham sp " +
                   "LEFT JOIN sp.chiTietDonHangs ctdh " +
                   "LEFT JOIN ctdh.donHang dh " +
                   "WHERE sp.trangThai = :trangThai AND (dh.trangThai = 'DA_GIAO' OR dh IS NULL) " +
                   "GROUP BY sp.maSanPham " +
                   "ORDER BY COALESCE(SUM(ctdh.soLuong), 0) DESC, sp.maSanPham ASC",
           countQuery = "SELECT COUNT(sp) FROM SanPham sp WHERE sp.trangThai = :trangThai")
    Page<Integer> findMaSanPhamBanChay(@Param("trangThai") TrangThaiSanPham trangThai, Pageable pageable);

    /**
     * Bước 2: Lấy đầy đủ thông tin chi tiết của các sản phẩm dựa trên danh sách ID.
     */
    @Query("SELECT sp FROM SanPham sp WHERE sp.maSanPham IN :ids")
    @EntityGraph(attributePaths = {"kho", "anhMinhHoas"})
    List<SanPham> findByIdsWithDetails(@Param("ids") List<Integer> ids);

    /**
     * Ghi đè phương thức findById để luôn fetch các quan hệ cần thiết cho trang chi tiết.
     * @param maSanPham ID của sản phẩm.
     * @return Optional chứa sản phẩm nếu tìm thấy.
     */
    @Override
    @EntityGraph(attributePaths = {"kho", "anhMinhHoas"})
    Optional<SanPham> findById(Integer maSanPham);
}