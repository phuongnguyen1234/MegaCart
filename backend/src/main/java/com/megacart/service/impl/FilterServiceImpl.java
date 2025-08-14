package com.megacart.service.impl;

import com.megacart.dto.response.FilterDataResponse;
import com.megacart.dto.response.PriceRangeResponse;
import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.model.DanhMuc;
import com.megacart.dto.response.DanhMucMenuItemResponse;
import com.megacart.repository.DanhMucRepository;
import com.megacart.repository.projection.PriceRangeProjection;
import com.megacart.repository.SanPhamRepository;
import com.megacart.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    private final SanPhamRepository sanPhamRepository;
    private final DanhMucRepository danhMucRepository;

    @Override
    @Transactional(readOnly = true)
    public FilterDataResponse getFilterData(String danhMucSlug, String tuKhoa) {
        List<String> nhaSanXuats;
        PriceRangeResponse khoangGia;
        List<DanhMucMenuItemResponse> danhMucs;

        if (StringUtils.hasText(danhMucSlug)) {
            Optional<DanhMuc> danhMucOpt = danhMucRepository.findBySlug(danhMucSlug);

            // Lấy nhà sản xuất theo danh mục
            nhaSanXuats = danhMucOpt
                    .map(danhMuc -> sanPhamRepository.findDistinctNhaSanXuatByDanhMuc(danhMuc.getMaDanhMuc(), TrangThaiSanPham.DANG_BAN))
                    .orElse(List.of());

            // Lấy khoảng giá theo danh mục
            khoangGia = danhMucOpt
                    .flatMap(danhMuc -> sanPhamRepository.findPriceRangeByDanhMuc(danhMuc.getMaDanhMuc(), TrangThaiSanPham.DANG_BAN))
                    .map(projection -> new PriceRangeResponse(projection.getMinPrice(), projection.getMaxPrice()))
                    .orElse(new PriceRangeResponse(0, 0)); // Mặc định nếu không có sản phẩm nào trong danh mục

            // Logic mới: Hiển thị các danh mục con, hoặc các danh mục anh em nếu không có con.
            danhMucs = danhMucOpt
                    .map(currentCategory -> {
                        // Xác định danh mục nào sẽ được dùng làm "cha" trong ngữ cảnh này
                        // để hiển thị các danh mục con của nó.
                        DanhMuc contextParentCategory;
                        if (!currentCategory.getDanhMucCons().isEmpty()) {
                            // Nếu danh mục hiện tại có con, thì nó chính là cha trong ngữ cảnh này.
                            contextParentCategory = currentCategory;
                        } else if (currentCategory.getDanhMucCha() != null) {
                            // Nếu là một "lá", thì cha của nó sẽ là cha trong ngữ cảnh này.
                            contextParentCategory = currentCategory.getDanhMucCha();
                        } else {
                            // Nếu là một "lá" và cũng là gốc, thì nó là cha của chính nó.
                            contextParentCategory = currentCategory;
                        }

                        List<DanhMucMenuItemResponse> categoryOptions = new java.util.ArrayList<>();

                        // 1. Thêm mục "Tất cả" cho danh mục cha theo ngữ cảnh
                        categoryOptions.add(DanhMucMenuItemResponse.builder()
                                .maDanhMuc(contextParentCategory.getMaDanhMuc())
                                .tenDanhMuc("Tất cả " + contextParentCategory.getTenDanhMuc())
                                .slug(contextParentCategory.getSlug())
                                .build());

                        // 2. Thêm danh sách các danh mục con của danh mục cha theo ngữ cảnh
                        List<DanhMucMenuItemResponse> subCategories = contextParentCategory.getDanhMucCons().stream()
                                .filter(subCat -> subCat.getTrangThai() == TrangThaiDanhMuc.HOAT_DONG)
                                .map(this::mapToMenuItem)
                                .toList();
                        categoryOptions.addAll(subCategories);
                        return categoryOptions;
                    })
                    .orElse(List.of());

        } else if (StringUtils.hasText(tuKhoa)) {
            // Trường hợp người dùng tìm kiếm bằng từ khóa
            // Lấy nhà sản xuất dựa trên kết quả tìm kiếm
            nhaSanXuats = sanPhamRepository.findDistinctNhaSanXuatByTuKhoa(tuKhoa, TrangThaiSanPham.DANG_BAN);

            // Lấy khoảng giá dựa trên kết quả tìm kiếm
            khoangGia = sanPhamRepository.findPriceRangeByTuKhoa(tuKhoa, TrangThaiSanPham.DANG_BAN)
                    .map(projection -> new PriceRangeResponse(projection.getMinPrice(), projection.getMaxPrice()))
                    .orElse(new PriceRangeResponse(0, 0));

            // Khi tìm kiếm, sidebar vẫn hiển thị các danh mục gốc để điều hướng
            danhMucs = danhMucRepository.findByDanhMucChaIsNullAndTrangThai(TrangThaiDanhMuc.HOAT_DONG)
                    .stream()
                    .map(this::mapToMenuItem)
                    .collect(Collectors.toList());

        } else {
            // Lấy tất cả nhà sản xuất
            nhaSanXuats = sanPhamRepository.findAllDistinctNhaSanXuat(TrangThaiSanPham.DANG_BAN);

            // Lấy khoảng giá tổng thể
            khoangGia = sanPhamRepository.findOverallPriceRange(TrangThaiSanPham.DANG_BAN)
                    .map(projection -> new PriceRangeResponse(projection.getMinPrice(), projection.getMaxPrice()))
                    .orElse(new PriceRangeResponse(0, 0)); // Mặc định nếu không có sản phẩm nào trong hệ thống

            // Khi không ở danh mục nào, chỉ hiển thị các danh mục GỐC (top-level)
            danhMucs = danhMucRepository.findByDanhMucChaIsNullAndTrangThai(TrangThaiDanhMuc.HOAT_DONG)
                    .stream()
                    .map(this::mapToMenuItem)
                    .collect(Collectors.toList());
        }

        return FilterDataResponse.builder()
                .nhaSanXuats(nhaSanXuats)
                .danhMucs(danhMucs)
                .khoangGia(khoangGia)
                .build();
    }

    private DanhMucMenuItemResponse mapToMenuItem(DanhMuc danhMuc) {
        return DanhMucMenuItemResponse.builder()
                .maDanhMuc(danhMuc.getMaDanhMuc())
                .tenDanhMuc(danhMuc.getTenDanhMuc())
                .slug(danhMuc.getSlug())
                .build();
    }
}