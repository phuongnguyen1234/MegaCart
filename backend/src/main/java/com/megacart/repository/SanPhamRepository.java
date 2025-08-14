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

public interface SanPhamRepository extends JpaRepository<SanPham, Integer>, JpaSpecificationExecutor<SanPham>, SanPhamRepositoryCustom {

    /**
     * Tìm kiếm tên sản phẩm để gợi ý (autocomplete) dựa trên tiền tố (prefix search).
     * Chỉ lấy tên sản phẩm để tối ưu performance và tận dụng index.
     * Tìm kiếm không phân biệt chữ hoa/thường.
     */
    // Bỏ hàm LOWER() vì CSDL đã được cấu hình Collation case-insensitive
    @Query("SELECT sp.tenSanPham FROM SanPham sp WHERE sp.tenSanPham LIKE :searchPattern AND sp.trangThai = :trangThai")
    List<String> findTenSanPhamByPrefixAndStatus(@Param("searchPattern") String searchPattern, @Param("trangThai") TrangThaiSanPham trangThai, Pageable pageable);

    /**
     * Ghi đè phương thức findById để luôn fetch các quan hệ cần thiết cho trang chi tiết.
     * @param maSanPham ID của sản phẩm.
     * @return Optional chứa sản phẩm nếu tìm thấy.
     */
    @Override
    @EntityGraph(attributePaths = {"kho", "anhMinhHoas"})
    Optional<SanPham> findById(Integer maSanPham);

    /**
     * Lấy danh sách các nhà sản xuất duy nhất cho một hoặc nhiều danh mục.
     * @param maDanhMucs Danh sách ID của các danh mục.
     * @param trangThai Trạng thái sản phẩm (ví dụ: BAN).
     * @return Danh sách các tên nhà sản xuất.
     */
    @Query("SELECT DISTINCT sp.nhaSanXuat FROM SanPham sp WHERE sp.danhMuc.maDanhMuc IN :maDanhMucs AND sp.trangThai = :trangThai ORDER BY sp.nhaSanXuat ASC")
    List<String> findDistinctNhaSanXuatByDanhMucIds(@Param("maDanhMucs") List<Integer> maDanhMucs, @Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy danh sách tất cả các nhà sản xuất duy nhất trong hệ thống.
     * @param trangThai Trạng thái sản phẩm (ví dụ: DANG_BAN).
     * @return Danh sách các tên nhà sản xuất.
     */
    @Query("SELECT DISTINCT sp.nhaSanXuat FROM SanPham sp WHERE sp.trangThai = :trangThai ORDER BY sp.nhaSanXuat ASC")
    List<String> findAllDistinctNhaSanXuat(@Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy khoảng giá (thấp nhất và cao nhất) cho một hoặc nhiều danh mục.
     * @param maDanhMucs Danh sách ID của các danh mục.
     * @param trangThai Trạng thái sản phẩm (ví dụ: BAN).
     * @return Một projection chứa minPrice và maxPrice.
     */
    @Query("SELECT MIN(sp.donGia) as minPrice, MAX(sp.donGia) as maxPrice FROM SanPham sp WHERE sp.danhMuc.maDanhMuc IN :maDanhMucs AND sp.trangThai = :trangThai")
    Optional<PriceRangeProjection> findPriceRangeByDanhMucIds(@Param("maDanhMucs") List<Integer> maDanhMucs, @Param("trangThai") TrangThaiSanPham trangThai);

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

    /**
     * Lấy danh sách các nhà sản xuất riêng biệt dựa trên nhãn sản phẩm.
     * @param nhan Nhãn sản phẩm (ví dụ: BAN_CHAY, MOI).
     * @param trangThai Trạng thái của sản phẩm (ví dụ: BAN).
     * @return Danh sách các nhà sản xuất.
     */
    @Query("SELECT DISTINCT sp.nhaSanXuat FROM SanPham sp WHERE sp.nhan = :nhan AND sp.trangThai = :trangThai ORDER BY sp.nhaSanXuat ASC")
    List<String> findDistinctNhaSanXuatByNhan(@Param("nhan") NhanSanPham nhan, @Param("trangThai") TrangThaiSanPham trangThai);

    /**
     * Lấy khoảng giá (min/max) dựa trên nhãn sản phẩm.
     * @param nhan Nhãn sản phẩm (ví dụ: BAN_CHAY, MOI).
     * @param trangThai Trạng thái của sản phẩm (ví dụ: BAN).
     * @return Một Optional chứa đối tượng PriceRangeProjection.
     */
    @Query("SELECT MIN(sp.donGia) as minPrice, MAX(sp.donGia) as maxPrice FROM SanPham sp WHERE sp.nhan = :nhan AND sp.trangThai = :trangThai")
    Optional<PriceRangeProjection> findPriceRangeByNhan(@Param("nhan") NhanSanPham nhan, @Param("trangThai") TrangThaiSanPham trangThai);
}