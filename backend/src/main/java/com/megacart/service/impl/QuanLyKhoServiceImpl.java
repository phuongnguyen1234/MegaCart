package com.megacart.service.impl;

import com.megacart.dto.response.KhoResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.model.DanhMuc;
import com.megacart.model.Kho;
import com.megacart.model.SanPham;
import com.megacart.repository.KhoRepository;
import com.megacart.service.DanhMucService;
import com.megacart.service.QuanLyKhoService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuanLyKhoServiceImpl implements QuanLyKhoService {

    private final KhoRepository khoRepository;
    private final DanhMucService danhMucService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<KhoResponse> getDanhSachKho(String tuKhoa, Integer maDanhMuc, Pageable pageable) {
        Specification<Kho> spec = (root, query, cb) -> {
            // Để tránh N+1, fetch sẵn các entity liên quan.
            // Từ Kho -> SanPham -> DanhMuc -> DanhMucCha
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("sanPham", JoinType.LEFT)
                        .fetch("danhMuc", JoinType.LEFT)
                        .fetch("danhMucCha", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo từ khóa (mã hoặc tên sản phẩm)
            if (StringUtils.hasText(tuKhoa)) {
                Predicate tenPredicate = cb.like(cb.lower(root.get("sanPham").get("tenSanPham")), "%" + tuKhoa.toLowerCase() + "%");
                Predicate maPredicate = tuKhoa.matches("\\d+")
                        ? cb.equal(root.get("maSanPham"), Integer.parseInt(tuKhoa))
                        : cb.disjunction(); // Predicate luôn false nếu không phải số
                predicates.add(cb.or(tenPredicate, maPredicate));
            }

            // Lọc theo danh mục (bao gồm cả danh mục con)
            if (maDanhMuc != null) {
                List<Integer> categoryIds = danhMucService.getAllSubCategoryIds(maDanhMuc);
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    predicates.add(root.get("sanPham").get("danhMuc").get("maDanhMuc").in(categoryIds));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Kho> khoPage = khoRepository.findAll(spec, pageable);

        List<KhoResponse> responses = khoPage.getContent().stream()
                .map(this::mapToKhoResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, khoPage.getNumber(), khoPage.getSize(), khoPage.getTotalElements(), khoPage.getTotalPages(), null);
    }

    private KhoResponse mapToKhoResponse(Kho kho) {
        SanPham sanPham = kho.getSanPham();
        DanhMuc danhMucCon = sanPham.getDanhMuc();
        DanhMuc danhMucCha = (danhMucCon != null) ? danhMucCon.getDanhMucCha() : null;

        return KhoResponse.builder()
                .maSanPham(kho.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .danhMucCha(danhMucCha != null ? danhMucCha.getTenDanhMuc() : null)
                .danhMucCon(danhMucCon != null ? danhMucCon.getTenDanhMuc() : null)
                .soLuong(kho.getSoLuong())
                .noiDungCapNhat(kho.getNoiDungCapNhat())
                .build();
    }
}