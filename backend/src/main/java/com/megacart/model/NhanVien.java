package com.megacart.model;

import com.megacart.enumeration.ViTri;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NhanVien")
public class NhanVien {

    @Id
    @Column(name = "MaNhanVien")
    private Integer maNhanVien;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Lấy giá trị ID từ mối quan hệ này
    @JoinColumn(name = "MaNhanVien")
    @ToString.Exclude // Ngăn vòng lặp vô tận khi gọi toString()
    private TaiKhoan taiKhoan;

    @Column(name = "HoTen", nullable = false)
    private String hoTen;

    @Enumerated(EnumType.STRING)
    @Column(name = "ViTri", nullable = false)
    private ViTri viTri;

    @OneToMany(mappedBy = "nhanVienGiaoHang")
    @ToString.Exclude
    private List<DonHang> donHangsDuocGiao = new ArrayList<>();

    // Helper methods để quản lý mối quan hệ hai chiều
    public void addDonHang(DonHang donHang) {
        donHangsDuocGiao.add(donHang);
        donHang.setNhanVienGiaoHang(this);
    }

    public void removeDonHang(DonHang donHang) {
        donHangsDuocGiao.remove(donHang);
        donHang.setNhanVienGiaoHang(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return maNhanVien != null && Objects.equals(maNhanVien, nhanVien.maNhanVien);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
