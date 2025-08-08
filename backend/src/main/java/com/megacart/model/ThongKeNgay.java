package com.megacart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ThongKeNgay")
public class ThongKeNgay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTKNgay")
    private Integer maTKNgay;

    @Column(name = "Ngay", nullable = false)
    private LocalDate ngay;

    @Column(name = "TongDoanhThu", nullable = false)
    private Integer tongDoanhThu;

    @Column(name = "SoDon", nullable = false)
    private Integer soDon;

    @Column(name = "SoSanPham", nullable = false)
    private Integer soSanPham;
}
