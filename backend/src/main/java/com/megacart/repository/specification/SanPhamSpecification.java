package com.megacart.repository.specification;

import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.model.SanPham;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SanPhamSpecification {

    // Sử dụng hằng số để tránh "magic strings" và giúp việc refactor dễ dàng hơn
    private static final String TRANG_THAI = "trangThai";
    private static final String TEN_SAN_PHAM = "tenSanPham";
    private static final String DANH_MUC = "danhMuc";
    private static final String MA_DANH_MUC = "maDanhMuc";
    private static final String DON_GIA = "donGia";
    private static final String NHA_SAN_XUAT = "nhaSanXuat";

    public Specification<SanPham> filterBy(
            String tuKhoa,
            Integer maDanhMuc,
            Integer giaToiDa,
            String nhaSanXuat
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn lọc theo trạng thái DANG_BAN
            predicates.add(criteriaBuilder.equal(root.get(TRANG_THAI), TrangThaiSanPham.DANG_BAN));

            // Lọc theo từ khóa (nếu có)
            if (tuKhoa != null && !tuKhoa.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get(TEN_SAN_PHAM), "%" + tuKhoa + "%"));
            }

            // Lọc theo danh mục (nếu có)
            if (maDanhMuc != null) {
                predicates.add(criteriaBuilder.equal(root.get(DANH_MUC).get(MA_DANH_MUC), maDanhMuc));
            }

            // Lọc theo giá tối đa (nếu có)
            if (giaToiDa != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(DON_GIA), giaToiDa));
            }

            // Lọc theo nhà sản xuất (nếu có)
            if (nhaSanXuat != null && !nhaSanXuat.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get(NHA_SAN_XUAT), nhaSanXuat));
            }

            // Kết hợp tất cả các điều kiện bằng AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}