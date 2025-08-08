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
@Table(name = "Kho")
public class Kho {

    @Id
    @Column(name = "MaSanPham")
    private Integer maSanPham;

    // Quan hệ 1-1 với SanPham, dùng chung khóa chính
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Đánh dấu rằng thuộc tính này dùng để ánh xạ khóa chính
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "NoiDungCapNhat")
    private String noiDungCapNhat;
}