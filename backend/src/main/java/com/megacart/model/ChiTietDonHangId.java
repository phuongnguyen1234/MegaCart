package com.megacart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangId implements Serializable {
    private Integer donHang; // Tên trường phải khớp với tên trường trong ChiTietDonHang
    private Integer sanPham; // Tên trường phải khớp với tên trường trong ChiTietDonHang
}