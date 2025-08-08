package com.megacart.model;

import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.enumeration.TrangThaiXuLi;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DonHang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDonHang")
    private Integer maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    @ToString.Exclude
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNVGiaoHang") // Sửa tên cột để khớp với CSDL
    @ToString.Exclude
    private NhanVien nhanVienGiaoHang;

    @Column(name = "TenKhachHang", nullable = false)
    private String tenKhachHang;

    @Column(name = "DiaChiNhanHang", nullable = false)
    private String diaChiNhanHang;

    @Column(name = "SdtNhanHang", nullable = false, length = 10)
    private String sdtNhanHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "HinhThucNhanHang", nullable = false)
    private HinhThucNhanHang hinhThucNhanHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "HinhThucThanhToan", nullable = false)
    private HinhThucThanhToan hinhThucThanhToan;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "ThoiGianDatHang", nullable = false)
    private LocalDateTime thoiGianDatHang;

    @Column(name = "DuKienGiaoHang")
    private LocalDateTime duKienGiaoHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false)
    private TrangThaiDonHang trangThai;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiXuLi", nullable = false)
    private TrangThaiXuLi trangThaiXuLi;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiThanhToan", nullable = false)
    private TrangThaiThanhToan trangThaiThanhToan;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default // **QUAN TRỌNG: Khởi tạo list để tránh NullPointerException khi dùng Builder**
    private List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();

    // Helper method để quản lý mối quan hệ hai chiều
    public void addChiTietDonHang(ChiTietDonHang chiTietDonHang) {
        chiTietDonHangs.add(chiTietDonHang);
        chiTietDonHang.setDonHang(this);
    }

    public void removeChiTietDonHang(ChiTietDonHang chiTietDonHang) {
        chiTietDonHangs.remove(chiTietDonHang);
        chiTietDonHang.setDonHang(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonHang donHang = (DonHang) o;
        return maDonHang != null && Objects.equals(maDonHang, donHang.maDonHang);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}