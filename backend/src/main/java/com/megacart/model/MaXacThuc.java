package com.megacart.model;

import com.megacart.enumeration.LoaiXacThuc;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "MaXacThuc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaXacThuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaXacThuc")
    private Integer maXacThuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTaiKhoan", nullable = false)
    private TaiKhoan taiKhoan;

    @Column(name = "MaOTP", nullable = false)
    private String maOTP;

    @Column(name = "ThoiGianHetHan", nullable = false)
    private Instant thoiGianHetHan;

    @Enumerated(EnumType.STRING)
    @Column(name = "Loai", nullable = false)
    private LoaiXacThuc loai;

    @Lob // Dùng cho các chuỗi dài, phù hợp để lưu JSON
    @Column(name = "DuLieuCho", columnDefinition = "TEXT") // Chỉ định rõ kiểu dữ liệu là TEXT để khớp với CSDL
    private String duLieuCho;
}