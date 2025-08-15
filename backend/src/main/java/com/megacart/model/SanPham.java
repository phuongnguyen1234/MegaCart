package com.megacart.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
@Table(name = "SanPham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSanPham")
    private Integer maSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDanhMucCon", nullable = false)
    @ToString.Exclude
    @JsonBackReference("danhmuc-sanpham")
    private DanhMuc danhMuc;

    @Column(name = "TenSanPham", nullable = false)
    private String tenSanPham;

    @Column(name = "MoTa")
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "Nhan") // Cho phép null vì không phải sản phẩm nào cũng có nhãn
    private NhanSanPham nhan;

    @Column(name = "BanChay", nullable = false)
    private boolean banChay;

    @Column(name = "NhaSanXuat", nullable = false)
    private String nhaSanXuat;

    @Column(name = "DonGia", nullable = false)
    private Integer donGia;

    @Column(name = "DonVi", nullable = false, length = 30)
    private String donVi;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false)
    private TrangThaiSanPham trangThai;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference("sanpham-anhminhhoa")
    private List<AnhMinhHoa> anhMinhHoas = new ArrayList<>();

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference("sanpham-chitietgiohang")
    private Set<ChiTietGioHang> chiTietGioHangs = new HashSet<>();

    @OneToMany(mappedBy = "sanPham")
    @ToString.Exclude
    @JsonManagedReference("sanpham-chitietdonhang")
    private List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();

    @OneToOne(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Kho kho;

    // Helper methods để quản lý mối quan hệ hai chiều một cách an toàn
    public void addChiTietDonHang(ChiTietDonHang chiTietDonHang) {
        chiTietDonHangs.add(chiTietDonHang);
        chiTietDonHang.setSanPham(this);
    }

    public void removeChiTietDonHang(ChiTietDonHang chiTietDonHang) {
        chiTietDonHangs.remove(chiTietDonHang);
        chiTietDonHang.setSanPham(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return maSanPham != null && Objects.equals(maSanPham, sanPham.maSanPham);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}