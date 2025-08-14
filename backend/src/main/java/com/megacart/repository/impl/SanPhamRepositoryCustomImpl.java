package com.megacart.repository.impl;

import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.ChiTietDonHang;
import com.megacart.model.DonHang;
import com.megacart.model.SanPham;
import com.megacart.repository.SanPhamRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SanPhamRepositoryCustomImpl implements SanPhamRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<SanPham> findBestSellingProducts(Specification<SanPham> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. Create Count Query to get total elements matching the filter
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<SanPham> countRoot = countQuery.from(SanPham.class);
        countQuery.select(cb.count(countRoot));
        if (spec != null) {
            countQuery.where(spec.toPredicate(countRoot, countQuery, cb));
        }
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // 2. Create Data Query to get the actual products, filtered and sorted by sales
        CriteriaQuery<SanPham> dataQuery = cb.createQuery(SanPham.class);
        Root<SanPham> dataRoot = dataQuery.from(SanPham.class);

        // Join to calculate sales
        Join<SanPham, ChiTietDonHang> chiTietJoin = dataRoot.join("chiTietDonHangs", JoinType.LEFT);
        Join<ChiTietDonHang, DonHang> donHangJoin = chiTietJoin.join("donHang", JoinType.LEFT);

        // Apply the dynamic filters from the Specification
        Predicate filterPredicate = spec != null ? spec.toPredicate(dataRoot, dataQuery, cb) : cb.conjunction();

        // Add condition to only count sales from delivered orders
        Predicate salesPredicate = cb.or(
                cb.equal(donHangJoin.get("trangThai"), TrangThaiDonHang.DA_GIAO),
                cb.isNull(donHangJoin) // Include products that have never been sold
        );

        dataQuery.where(cb.and(filterPredicate, salesPredicate));

        // Group by product to sum up sales
        dataQuery.groupBy(dataRoot.get("maSanPham"));

        // Dynamic Order By:
        // Nếu người dùng cung cấp một thứ tự sắp xếp (ví dụ: theo giá), hãy sử dụng nó.
        // Nếu không, mặc định sắp xếp theo số lượng bán chạy nhất.
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            // Chuyển đổi Sort object của Spring thành Order list của JPA Criteria
            List<Order> orders = sort.stream()
                    .map(order -> order.isAscending() ? cb.asc(dataRoot.get(order.getProperty())) : cb.desc(dataRoot.get(order.getProperty())))
                    .toList();
            dataQuery.orderBy(orders);
        } else {
            // Logic sắp xếp mặc định cho trang "Bán chạy"
            Expression<Long> totalSold = cb.coalesce(cb.sum(chiTietJoin.get("soLuong")), 0L);
            dataQuery.orderBy(cb.desc(totalSold), cb.asc(dataRoot.get("maSanPham")));
        }

        // Select the product entity
        dataQuery.select(dataRoot);

        // 3. Execute the data query with pagination
        TypedQuery<SanPham> typedQuery = entityManager.createQuery(dataQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<SanPham> products = typedQuery.getResultList();

        // 4. Return the Page object
        return new PageImpl<>(products, pageable, total);
    }
}