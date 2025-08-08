package com.megacart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChiTietGioHangId implements Serializable {
    @Column(name = "MaSanPham")
    private Integer maSanPham;

    @Column(name = "MaKhachHang")
    private Integer maKhachHang;
}