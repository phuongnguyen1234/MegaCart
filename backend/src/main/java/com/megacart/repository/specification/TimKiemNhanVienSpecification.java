package com.megacart.repository.specification;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.enumeration.ViTri;
import com.megacart.model.NhanVien;
import com.megacart.model.TaiKhoan;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimKiemNhanVienSpecification {

    private static final String TAI_KHOAN = "taiKhoan";

    public Specification<NhanVien> filterBy(String searchField, String searchValue, ViTri viTri, boolean hienThiTaiKhoanBiKhoa) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<NhanVien, TaiKhoan> taiKhoanJoin = root.join(TAI_KHOAN);

            // Lọc theo trạng thái tài khoản
            if (hienThiTaiKhoanBiKhoa) {
                predicates.add(criteriaBuilder.equal(taiKhoanJoin.get("trangThaiTaiKhoan"), TrangThaiTaiKhoan.KHOA));
            } else {
                predicates.add(criteriaBuilder.equal(taiKhoanJoin.get("trangThaiTaiKhoan"), TrangThaiTaiKhoan.HOAT_DONG));
            }

            // Lọc theo vị trí (nếu có)
            if (viTri != null) {
                predicates.add(criteriaBuilder.equal(root.get("viTri"), viTri));
            }

            // Lọc theo các trường tìm kiếm
            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                switch (searchField) {
                    case "maNhanVien":
                        try {
                            predicates.add(criteriaBuilder.equal(root.get("maNhanVien"), Integer.parseInt(searchValue)));
                        } catch (NumberFormatException e) {
                            predicates.add(criteriaBuilder.disjunction());
                        }
                        break;
                    case "hoTen":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("hoTen")), "%" + searchValue.toLowerCase() + "%"));
                        break;
                    case "email":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(taiKhoanJoin.get("email")), "%" + searchValue.toLowerCase() + "%"));
                        break;
                    case "soDienThoai":
                        predicates.add(criteriaBuilder.like(taiKhoanJoin.get("soDienThoai"), "%" + searchValue + "%"));
                        break;
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}