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
import com.megacart.repository.projection.PriceRangeProjection;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer>, JpaSpecificationExecutor<SanPham> {

    /**
     * Tìm kiếm tên sản phẩm để gợi ý (autocomplete) dựa trên tiền tố (prefix search).
     * Chỉ lấy tên sản phẩm để tối ưu performance và tận dụng index.
     * Tìm kiếm không phân biệt chữ hoa/thường.
     */
    // Bỏ hàm LOWER() vì CSDL đã được cấu hình Collation case-insensitive
    @Query("SELECT sp.tenSanPham FROM SanPham sp WHERE sp.tenSanPham LIKE :searchPattern AND sp.trangThai = :trangThai")
    List<String> findTenSanPhamByPrefixAndStatus(@Param("searchPattern") String searchPattern, @Param("trangThai") TrangThaiSanPham trangThai, Pageable pageable);

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

    /**
     * Lấy danh sách các nhà sản xuất duy nhất cho một danh mục cụ thể.
     * @param maDanhMuc ID của danh mục.
     * @param trangThai Trạng thái sản phẩm (ví dụ: DANG_BAN).
     * @return Danh sách các tên nhà sản xuất.
     */
    @Query("SELECT DISTINCT sp.nhaSanXuat FROM SanPham sp WHERE sp.danhMuc.maDanhMuc = :maDanhMuc AND sp.trangThai = :trangThai ORDER BY sp.nhaSanXuat ASC")
    List<String> findDistinctNhaSanXuatByDanhMuc(@Param("maDanhMuc") Integer maDanhMuc, @Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy danh sách tất cả các nhà sản xuất duy nhất trong hệ thống.
     * @param trangThai Trạng thái sản phẩm (ví dụ: DANG_BAN).
     * @return Danh sách các tên nhà sản xuất.
     */
    @Query("SELECT DISTINCT sp.nhaSanXuat FROM SanPham sp WHERE sp.trangThai = :trangThai ORDER BY sp.nhaSanXuat ASC")
    List<String> findAllDistinctNhaSanXuat(@Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy khoảng giá (thấp nhất và cao nhất) cho một danh mục cụ thể.
     * @param maDanhMuc ID của danh mục.
     * @param trangThai Trạng thái sản phẩm (ví dụ: DANG_BAN).
     * @return Một projection chứa minPrice và maxPrice.
     */
    @Query("SELECT MIN(sp.donGia) as minPrice, MAX(sp.donGia) as maxPrice FROM SanPham sp WHERE sp.danhMuc.maDanhMuc = :maDanhMuc AND sp.trangThai = :trangThai")
    Optional<PriceRangeProjection> findPriceRangeByDanhMuc(@Param("maDanhMuc") Integer maDanhMuc, @Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy khoảng giá (thấp nhất và cao nhất) cho tất cả sản phẩm.
     * @param trangThai Trạng thái sản phẩm (ví dụ: DANG_BAN).
     * @return Một projection chứa minPrice và maxPrice.
     */
    @Query("SELECT MIN(sp.donGia) as minPrice, MAX(sp.donGia) as maxPrice FROM SanPham sp WHERE sp.trangThai = :trangThai")
    Optional<PriceRangeProjection> findOverallPriceRange(@Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy danh sách các nhà sản xuất duy nhất cho các sản phẩm khớp với từ khóa.
     * @param tuKhoa Từ khóa tìm kiếm.
     * @param trangThai Trạng thái sản phẩm.
     * @return Danh sách các tên nhà sản xuất.
     */
    @Query("SELECT DISTINCT sp.nhaSanXuat FROM SanPham sp WHERE sp.tenSanPham LIKE %:tuKhoa% AND sp.trangThai = :trangThai ORDER BY sp.nhaSanXuat ASC")
    List<String> findDistinctNhaSanXuatByTuKhoa(@Param("tuKhoa") String tuKhoa, @Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy khoảng giá cho các sản phẩm khớp với từ khóa.
     * @param tuKhoa Từ khóa tìm kiếm.
     * @param trangThai Trạng thái sản phẩm.
     * @return Một projection chứa minPrice và maxPrice.
     */
    @Query("SELECT MIN(sp.donGia) as minPrice, MAX(sp.donGia) as maxPrice FROM SanPham sp WHERE sp.tenSanPham LIKE %:tuKhoa% AND sp.trangThai = :trangThai")
    Optional<PriceRangeProjection> findPriceRangeByTuKhoa(@Param("tuKhoa") String tuKhoa, @Param("trangThai") TrangThaiSanPham trangThai);
}