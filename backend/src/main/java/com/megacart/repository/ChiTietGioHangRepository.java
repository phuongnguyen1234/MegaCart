package com.megacart.repository;

import com.megacart.model.ChiTietGioHang;
import com.megacart.model.ChiTietGioHangId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, ChiTietGioHangId> {

    /**
     * Xóa một sản phẩm khỏi giỏ hàng của khách hàng.
     * clearAutomatically = true sẽ xóa persistence context sau khi query,
     * buộc lần đọc tiếp theo phải lấy dữ liệu mới từ CSDL, tránh lỗi cache không đồng bộ.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ChiTietGioHang c WHERE c.gioHang.maKhachHang = :maKhachHang AND c.sanPham.maSanPham = :maSanPham")
    void deleteByKhachHangAndSanPham(@Param("maKhachHang") Integer maKhachHang, @Param("maSanPham") Integer maSanPham);
}