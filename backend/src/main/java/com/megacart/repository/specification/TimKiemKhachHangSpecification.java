package com.megacart.repository.specification;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.model.KhachHang;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimKiemKhachHangSpecification {

    public Specification<KhachHang> filterBy(String searchField, String searchValue, TrangThaiTaiKhoan trangThai) {
        return (root, query, cb) -> {
            // Để tránh N+1 query, fetch sẵn tài khoản
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("taiKhoan", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo trạng thái tài khoản. Nếu trangThai là null, không áp dụng bộ lọc này.
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("taiKhoan").get("trangThaiTaiKhoan"), trangThai));
            }

            // Lọc theo trường tìm kiếm và giá trị tìm kiếm
            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                switch (searchField.toLowerCase()) {
                    case "tenkhachhang" -> predicates.add(cb.like(cb.lower(root.get("tenKhachHang")), "%" + searchValue.toLowerCase() + "%"));
                    case "email" -> predicates.add(cb.like(cb.lower(root.get("taiKhoan").get("email")), "%" + searchValue.toLowerCase() + "%"));
                    case "sodienthoai" -> predicates.add(cb.like(root.get("taiKhoan").get("soDienThoai"), "%" + searchValue + "%"));
                    // Có thể thêm các trường tìm kiếm khác ở đây
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}