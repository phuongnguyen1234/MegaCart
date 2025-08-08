package com.megacart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GioHang")
public class GioHang {

    @Id
    @Column(name = "MaKhachHang")
    private Integer maKhachHang;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "MaKhachHang")
    @ToString.Exclude
    private KhachHang khachHang;

    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ChiTietGioHang> chiTietGioHangs = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GioHang gioHang = (GioHang) o;
        return maKhachHang != null && Objects.equals(maKhachHang, gioHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
