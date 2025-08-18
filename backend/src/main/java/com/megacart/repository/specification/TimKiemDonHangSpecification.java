package com.megacart.repository.specification;

import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.DonHang;
import com.megacart.model.KhachHang;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimKiemDonHangSpecification {

    public Specification<DonHang> filterBy(String searchField, String searchValue, TrangThaiDonHang trangThai, LocalDate ngayDat) {
        return (root, query, cb) -> {
            // Để tránh N+1 query, fetch sẵn khách hàng
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("khachHang", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo trạng thái đơn hàng
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            // Lọc theo một ngày cụ thể (từ 00:00:00 đến 23:59:59.999)
            if (ngayDat != null) {
                java.time.Instant startOfDay = ngayDat.atStartOfDay(ZoneId.systemDefault()).toInstant();
                java.time.Instant endOfDay = ngayDat.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                predicates.add(cb.between(root.get("thoiGianDatHang"), startOfDay, endOfDay));
            }

            // Lọc theo trường tìm kiếm và giá trị tìm kiếm
            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                switch (searchField.toLowerCase()) {
                    case "madonhang":
                        if (searchValue.matches("\\d+")) {
                            predicates.add(cb.equal(root.get("maDonHang"), Integer.parseInt(searchValue)));
                        }
                        break;
                    case "tenkhachhang":
                        Join<DonHang, KhachHang> khachHangJoin = root.join("khachHang");
                        predicates.add(cb.like(cb.lower(khachHangJoin.get("tenKhachHang")), "%" + searchValue.toLowerCase() + "%"));
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}