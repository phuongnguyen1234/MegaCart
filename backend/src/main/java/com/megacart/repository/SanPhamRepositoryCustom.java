package com.megacart.repository;

import com.megacart.model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SanPhamRepositoryCustom {
    Page<SanPham> findBestSellingProducts(Specification<SanPham> spec, Pageable pageable);
}