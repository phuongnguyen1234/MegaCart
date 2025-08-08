package com.megacart.repository;

import com.megacart.model.GioHang;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    /**
     * Tìm giỏ hàng của khách hàng và tải sẵn các thông tin liên quan để tránh lỗi N+1.
     * - chiTietGioHangs: Danh sách các sản phẩm trong giỏ.
     * - sanPham, anhMinhHoas, kho: Thông tin chi tiết của từng sản phẩm.
     */
    @EntityGraph(attributePaths = {"chiTietGioHangs.sanPham.anhMinhHoas", "chiTietGioHangs.sanPham.kho", "khachHang.taiKhoan"})
    Optional<GioHang> findByKhachHang_MaKhachHang(Integer maKhachHang);
}