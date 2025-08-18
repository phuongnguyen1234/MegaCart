package com.megacart.repository;

import com.megacart.model.ChiTietDonHang;
import com.megacart.model.ChiTietDonHangId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, ChiTietDonHangId> {
    // Lấy tất cả chi tiết đơn hàng cho một danh sách các mã đơn hàng
    // Dùng EntityGraph để fetch sẵn sản phẩm, tránh N+1 query khi truy cập sanPhamEntity
    @EntityGraph(attributePaths = {"sanPham"})
    List<ChiTietDonHang> findByDonHang_MaDonHangIn(List<Integer> donHangIds);
}