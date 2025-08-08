package com.megacart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ChiTietGioHang")
public class ChiTietGioHang {

    @EmbeddedId
    private ChiTietGioHangId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSanPham")
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maKhachHang")
    @JoinColumn(name = "MaKhachHang")
    private GioHang gioHang;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;
}