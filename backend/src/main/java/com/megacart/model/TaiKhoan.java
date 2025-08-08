package com.megacart.model;

import com.megacart.enumeration.QuyenTruyCap;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTaiKhoan")
    private Integer maTaiKhoan;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "SoDienThoai", unique = true, length = 10) // Cho phép null
    private String soDienThoai;

    @Column(name = "MatKhau", nullable = false, length = 255)
    private String matKhau;

    @Enumerated(EnumType.STRING)
    @Column(name = "QuyenTruyCap", nullable = false, length = 255)
    private QuyenTruyCap quyenTruyCap;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiTaiKhoan", nullable = false, length = 255)
    private TrangThaiTaiKhoan trangThaiTaiKhoan;

    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // **QUAN TRỌNG: Ngắt vòng lặp trong toString()**
    private KhachHang khachHang;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(quyenTruyCap.name()));
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return trangThaiTaiKhoan != TrangThaiTaiKhoan.KHOA; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return trangThaiTaiKhoan == TrangThaiTaiKhoan.HOAT_DONG; }

    public String getTenKhachHangMacDinh() {
        return this.email != null ? this.email.split("@")[0] : "User";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        return maTaiKhoan != null && Objects.equals(maTaiKhoan, taiKhoan.maTaiKhoan);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // **QUAN TRỌNG: Cách làm an toàn cho JPA entities**
    }
}
