package com.megacart.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ChiTietDonHang")
@IdClass(ChiTietDonHangId.class)
public class ChiTietDonHang {

    // --- CÂY CẦU NỐI TỚI DỮ LIỆU HIỆN TẠI ---
    // Các mối quan hệ này được giữ lại để phục vụ cho việc phân tích, báo cáo,
    // và các tính năng như "Mua lại". Chúng không được dùng để hiển thị thông tin đơn hàng cũ.
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang", referencedColumnName = "MaDonHang")
    @ToString.Exclude
    private DonHang donHang;


    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSanPham", referencedColumnName = "MaSanPham")
    @ToString.Exclude
    private SanPham sanPham;

    // --- DỮ LIỆU SNAPSHOT (BẢN GHI LỊCH SỬ) ---
    // Các trường dưới đây sao chép thông tin sản phẩm tại thời điểm đặt hàng
    // để đảm bảo tính toàn vẹn của đơn hàng, không bị ảnh hưởng bởi các thay đổi trong tương lai.
    @Column(name = "TenSanPham", nullable = false)
    private String tenSanPham;

    @Column(name = "AnhMinhHoa")
    private String anhMinhHoa;

    @Column(name = "DonGia", nullable = false)
    private Integer donGia;

    @Column(name = "DonVi", nullable = false, length = 30)
    private String donVi;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDonHang that = (ChiTietDonHang) o;
        if (donHang != null ? !donHang.getMaDonHang().equals(that.donHang.getMaDonHang()) : that.donHang != null) return false;
        return sanPham != null ? sanPham.getMaSanPham().equals(that.sanPham.getMaSanPham()) : that.sanPham == null;
    }

    @Override
    public int hashCode() {
        int result = donHang != null ? donHang.getMaDonHang().hashCode() : 0;
        result = 31 * result + (sanPham != null ? sanPham.getMaSanPham().hashCode() : 0);
        return result;
    }
}