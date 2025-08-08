package com.megacart.model;

import com.megacart.enumeration.DonVi;
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

    // Sao chép thông tin sản phẩm tại thời điểm đặt hàng để đảm bảo tính toàn vẹn
    @Column(name = "TenSanPham", nullable = false)
    private String tenSanPham;

    @Column(name = "AnhMinhHoa")
    private String anhMinhHoa;

    @Column(name = "NhaSanXuat", nullable = false)
    private String nhaSanXuat;

    @Column(name = "DonGia", nullable = false)
    private Integer donGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "DonVi", nullable = false, length = 30)
    private DonVi donVi;

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