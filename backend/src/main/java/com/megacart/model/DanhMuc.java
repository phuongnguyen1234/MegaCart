package com.megacart.model;

import com.megacart.enumeration.TrangThaiDanhMuc;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DanhMuc")
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDanhMuc")
    private Integer maDanhMuc;

    // Quan hệ tự tham chiếu: Một danh mục cha có thể có nhiều danh mục con
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDanhMucCha")
    private DanhMuc danhMucCha;

    @OneToMany(mappedBy = "danhMucCha")
    @ToString.Exclude // Tránh lỗi lặp vô hạn khi gọi toString()
    private List<DanhMuc> danhMucCons = new ArrayList<>();

    @Column(name = "TenDanhMuc")
    private String tenDanhMuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false)
    private TrangThaiDanhMuc trangThai;

    // Một danh mục có thể chứa nhiều sản phẩm
    @OneToMany(mappedBy = "danhMuc")
    private List<SanPham> sanPhams = new ArrayList<>();
}