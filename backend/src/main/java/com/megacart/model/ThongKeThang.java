package com.megacart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ThongKeThang")
public class ThongKeThang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTKThang")
    private Integer maTKThang;

    @Column(name = "Thang", nullable = false)
    private Integer thang;

    @Column(name = "Nam", nullable = false)
    private Integer nam;

    @Column(name = "MucTieuDoanhThu", nullable = false)
    private Integer mucTieuDoanhThu;

    @Column(name = "DoanhThu", nullable = false)
    private Integer doanhThu;

    @Column(name = "TangTruongDoanhThu", nullable = false)
    private Double tangTruongDoanhThu;

    @Column(name = "SoDonHang", nullable = false)
    private Integer soDonHang;

    @Column(name = "TangTruongDonHang", nullable = false)
    private Double tangTruongDonHang;

    @Column(name = "SoSanPham", nullable = false)
    private Integer soSanPham;

    @Column(name = "TrungBinhMoiDon", nullable = false, precision = 15, scale = 3)
    private BigDecimal trungBinhMoiDon;

    @Column(name = "DonChoXacNhan", nullable = false)
    private Integer donChoXacNhan;

    @Column(name = "DonChoXuLi", nullable = false)
    private Integer donChoXuLi;

    @Column(name = "DonDangGiao", nullable = false)
    private Integer donDangGiao;

    @Column(name = "DonDaGiao", nullable = false)
    private Integer donDaGiao;

    @Column(name = "DaDaHuy", nullable = false)
    private Integer daDaHuy;
}
