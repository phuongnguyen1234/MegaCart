package com.megacart.repository.specification;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.model.KhachHang;
import com.megacart.model.TaiKhoan;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimKiemKhachHangSpecification {

    private static final String TAI_KHOAN = "taiKhoan";

    public Specification<KhachHang> filterBy(String searchField, String searchValue, boolean hienThiTaiKhoanBiKhoa) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<KhachHang, TaiKhoan> taiKhoanJoin = root.join(TAI_KHOAN);

            // Lọc theo trạng thái tài khoản một cách linh hoạt
            if (hienThiTaiKhoanBiKhoa) {
                // Nếu checkbox được chọn, chỉ hiển thị các tài khoản có trạng thái là KHOA
                predicates.add(criteriaBuilder.equal(taiKhoanJoin.get("trangThaiTaiKhoan"), TrangThaiTaiKhoan.KHOA));
            } else {
                // Mặc định, chỉ lấy tài khoản HOAT_DONG
                predicates.add(criteriaBuilder.equal(taiKhoanJoin.get("trangThaiTaiKhoan"), TrangThaiTaiKhoan.HOAT_DONG));
            }

            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                switch (searchField) {
                    case "maKhachHang":
                        try {
                            predicates.add(criteriaBuilder.equal(root.get("maKhachHang"), Integer.parseInt(searchValue)));
                        } catch (NumberFormatException e) {
                            // Nếu giá trị tìm kiếm không phải là số, không thể khớp với mã khách hàng.
                            // Tạo một điều kiện luôn sai để không trả về kết quả nào.
                            predicates.add(criteriaBuilder.disjunction());
                        }
                        break;
                    case "tenKhachHang":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("tenKhachHang")), "%" + searchValue.toLowerCase() + "%"));
                        break;
                    case "email":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(taiKhoanJoin.get("email")), "%" + searchValue.toLowerCase() + "%"));
                        break;
                    case "diaChi":
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("diaChi")), "%" + searchValue.toLowerCase() + "%"));
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