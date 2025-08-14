package com.megacart.model;

import com.megacart.enumeration.TrangThaiDanhMuc;
import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "DanhMuc")
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDanhMuc")
    private Integer maDanhMuc;

    // Quan hệ tự tham chiếu: Một danh mục cha có thể có nhiều danh mục con
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDanhMucCha")
    @ToString.Exclude // Tránh lỗi lặp vô hạn khi gọi toString()
    private DanhMuc danhMucCha;

    @OneToMany(mappedBy = "danhMucCha")
    @ToString.Exclude // Tránh lỗi lặp vô hạn khi gọi toString()
    @Builder.Default
    private List<DanhMuc> danhMucCons = new ArrayList<>();

    @Column(name = "TenDanhMuc")
    private String tenDanhMuc;

    @Column(name = "Slug", nullable = false, unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false)
    private TrangThaiDanhMuc trangThai;

    // Một danh mục có thể chứa nhiều sản phẩm
    @OneToMany(mappedBy = "danhMuc")
    @ToString.Exclude
    @Builder.Default
    private List<SanPham> sanPhams = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DanhMuc danhMuc = (DanhMuc) o;
        return maDanhMuc != null && Objects.equals(maDanhMuc, danhMuc.maDanhMuc);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}