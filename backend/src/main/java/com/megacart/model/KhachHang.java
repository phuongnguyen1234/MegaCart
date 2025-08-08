package com.megacart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @Column(name = "MaKhachHang")
    private Integer maKhachHang;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Lấy giá trị ID từ mối quan hệ này
    @JoinColumn(name = "MaKhachHang")
    @ToString.Exclude
    private TaiKhoan taiKhoan;

    @Column(name = "TenKhachHang", nullable = false)
    private String tenKhachHang;

    @Column(name = "DiaChi")
    private String diaChi;

    @OneToOne(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private GioHang gioHang;

    // Helper method để đồng bộ hóa mối quan hệ hai chiều
    public void setGioHang(GioHang gioHang) {
        if (gioHang == null) {
            if (this.gioHang != null) {
                this.gioHang.setKhachHang(null);
            }
        } else {
            gioHang.setKhachHang(this);
        }
        this.gioHang = gioHang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return maKhachHang != null && Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
