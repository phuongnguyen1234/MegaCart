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
            List<Integer> maDanhMucs,
            Integer giaToiDa,
            String nhaSanXuat
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn lọc theo trạng thái BAN
            predicates.add(criteriaBuilder.equal(root.get(TRANG_THAI), TrangThaiSanPham.BAN));

            // Lọc theo từ khóa (nếu có)
            if (tuKhoa != null && !tuKhoa.isBlank()) {
                predicates.add(criteriaBuilder.like(root.get(TEN_SAN_PHAM), "%" + tuKhoa + "%"));
            }

            // Lọc theo danh mục (nếu có)
            if (maDanhMucs != null && !maDanhMucs.isEmpty()) {
                predicates.add(root.get(DANH_MUC).get(MA_DANH_MUC).in(maDanhMucs));
            }

            // Lọc theo giá tối đa (nếu có)
            if (giaToiDa != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(DON_GIA), giaToiDa));
            }

            // Lọc theo nhà sản xuất (nếu có)
            if (nhaSanXuat != null && !nhaSanXuat.isBlank()) {
                // Sử dụng 'like' thay vì 'equal' để tìm kiếm linh hoạt hơn.
                // Ví dụ: tìm "Apple" sẽ ra kết quả "Apple Inc."
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(NHA_SAN_XUAT)), "%" + nhaSanXuat.toLowerCase() + "%"));
            }

            // Kết hợp tất cả các điều kiện bằng AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}