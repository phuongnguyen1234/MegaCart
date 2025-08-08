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
@Table(name = "AnhMinhHoa")
public class AnhMinhHoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaAnh")
    private Integer maAnh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSanPham", nullable = false)
    private SanPham sanPham;

    @Column(name = "DuongDan", nullable = false)
    private String duongDan;

    @Column(name = "LaAnhChinh", nullable = false)
    private boolean laAnhChinh;

    @Column(name = "SoThuTu", nullable = false)
    private Integer soThuTu;
}