package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatDanhMucRequest;
import com.megacart.dto.response.ChiTietDanhMucQuanLyResponse;
import com.megacart.dto.request.ThemDanhMucRequest;
import com.megacart.dto.response.DanhMucQuanLyResponse;
import com.megacart.dto.response.DanhMucOptionResponse;
import com.megacart.dto.response.DanhMucMenuItemResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.enumeration.TrangThaiSanPham;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.DanhMuc;
import com.megacart.repository.DanhMucRepository;
import com.megacart.repository.SanPhamRepository;
import com.megacart.service.DanhMucService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DanhMucServiceImpl implements DanhMucService {

    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;

    // Khởi tạo Collator cho tiếng Việt để sắp xếp đúng chuẩn
    private static final Collator VIETNAMESE_COLLATOR = Collator.getInstance(new Locale("vi", "VN"));

    @Override
    @Transactional(readOnly = true)
    public List<DanhMucMenuItemResponse> getMenuDanhMucs() {
        final int SUB_CATEGORY_LIMIT = 12;

        // 1. Lấy tất cả danh mục đang hoạt động, dùng JOIN FETCH để lấy luôn danh mục cha
        List<DanhMuc> allActiveCategories = danhMucRepository.findAllByTrangThaiWithParent(TrangThaiDanhMuc.HOAT_DONG);

        // 2. Nhóm các danh mục con theo ID của cha để tra cứu hiệu quả
        Map<Integer, List<DanhMuc>> childrenByParentId = allActiveCategories.stream()
                .filter(dm -> dm.getDanhMucCha() != null)
                .collect(Collectors.groupingBy(dm -> dm.getDanhMucCha().getMaDanhMuc()));

        // 3. Lấy ra các danh mục gốc (không có cha)
        List<DanhMuc> rootCategoryEntities = allActiveCategories.stream()
                .filter(dm -> dm.getDanhMucCha() == null)
                .sorted(Comparator.comparing(DanhMuc::getTenDanhMuc, VIETNAMESE_COLLATOR)) // Sắp xếp danh mục cha
                .toList();

        // 4. Xây dựng cây DTO từ các danh mục gốc, áp dụng giới hạn cho các danh mục con
        return rootCategoryEntities.stream()
                .map(rootEntity -> {
                    // Lấy danh sách con đầy đủ của danh mục gốc này
                    List<DanhMuc> children = childrenByParentId.getOrDefault(rootEntity.getMaDanhMuc(), Collections.emptyList());

                    // Sắp xếp danh mục con theo tên (A-Z) trước khi giới hạn số lượng
                    List<DanhMucMenuItemResponse> limitedChildrenDTOs = children.stream()
                            .sorted(Comparator.comparing(DanhMuc::getTenDanhMuc, VIETNAMESE_COLLATOR))
                            .limit(SUB_CATEGORY_LIMIT)
                            .map(this::mapToMenuItem)
                            .collect(Collectors.toList());

                    // Xây dựng DTO cho danh mục gốc
                    return DanhMucMenuItemResponse.builder()
                            .maDanhMuc(rootEntity.getMaDanhMuc())
                            .tenDanhMuc(rootEntity.getTenDanhMuc())
                            .slug(rootEntity.getSlug())
                            .danhMucCons(limitedChildrenDTOs.isEmpty() ? null : limitedChildrenDTOs)
                            // Đặt cờ 'hasMoreChildren' nếu số lượng con thực tế > giới hạn,
                            // để frontend biết có cần hiển thị nút "Xem thêm" hay không.
                            .hasMoreChildren(children.size() > SUB_CATEGORY_LIMIT ? true : null) // Chỉ hiển thị khi là true
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAllSubCategoryIds(Integer parentId) {
        // 1. Fetch all categories in one go to build the tree in memory
        List<DanhMuc> allCategories = danhMucRepository.findAll();

        // 2. Create a map of parentId -> list of children for quick lookup
        Map<Integer, List<DanhMuc>> parentToChildrenMap = allCategories.stream()
                .filter(c -> c.getDanhMucCha() != null)
                .collect(Collectors.groupingBy(c -> c.getDanhMucCha().getMaDanhMuc()));

        // 3. Find the starting category node
        Optional<DanhMuc> startNodeOpt = allCategories.stream()
                .filter(c -> c.getMaDanhMuc().equals(parentId))
                .findFirst();

        if (startNodeOpt.isEmpty()) {
            // If the provided ID doesn't exist, just search for that ID.
            return List.of(parentId);
        }

        // 4. Traverse the tree from the start node to collect all descendant IDs
        List<Integer> resultIds = new ArrayList<>();
        collectIdsRecursively(startNodeOpt.get(), resultIds, parentToChildrenMap);
        return resultIds;
    }

    private void collectIdsRecursively(DanhMuc currentNode, List<Integer> resultIds, Map<Integer, List<DanhMuc>> parentToChildrenMap) {
        resultIds.add(currentNode.getMaDanhMuc());
        List<DanhMuc> children = parentToChildrenMap.getOrDefault(currentNode.getMaDanhMuc(), Collections.emptyList());
        for (DanhMuc child : children) {
            collectIdsRecursively(child, resultIds, parentToChildrenMap);
        }
    }

    private DanhMucMenuItemResponse mapToMenuItem(DanhMuc danhMuc) {
        return DanhMucMenuItemResponse.builder()
                .maDanhMuc(danhMuc.getMaDanhMuc())
                .tenDanhMuc(danhMuc.getTenDanhMuc())
                .slug(danhMuc.getSlug())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhMucOptionResponse> getDanhMucOptionsForFilter() {
        List<DanhMuc> allActiveCategories = danhMucRepository.findAllByTrangThaiWithParent(TrangThaiDanhMuc.HOAT_DONG);

        Map<Integer, List<DanhMuc>> childrenByParentId = allActiveCategories.stream()
                .filter(dm -> dm.getDanhMucCha() != null)
                .collect(Collectors.groupingBy(dm -> dm.getDanhMucCha().getMaDanhMuc()));

        List<DanhMuc> rootCategories = allActiveCategories.stream()
                .filter(dm -> dm.getDanhMucCha() == null)
                .sorted(Comparator.comparing(DanhMuc::getTenDanhMuc, VIETNAMESE_COLLATOR)) // Sắp xếp danh mục cha
                .toList();

        List<DanhMucOptionResponse> options = new ArrayList<>();
        for (DanhMuc root : rootCategories) {
            addCategoryToOptions(root, 0, options, childrenByParentId);
        }
        return options;
    }

    private void addCategoryToOptions(DanhMuc category, int level, List<DanhMucOptionResponse> options, Map<Integer, List<DanhMuc>> childrenByParentId) {
        String prefix = "  ".repeat(level);
        options.add(new DanhMucOptionResponse(category.getMaDanhMuc(), prefix + (level > 0 ? "- " : "") + category.getTenDanhMuc()));

        List<DanhMuc> children = childrenByParentId.getOrDefault(category.getMaDanhMuc(), Collections.emptyList());
        children.sort(Comparator.comparing(DanhMuc::getTenDanhMuc, VIETNAMESE_COLLATOR)); // Sắp xếp danh mục con
        for (DanhMuc child : children) {
            addCategoryToOptions(child, level + 1, options, childrenByParentId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DanhMucQuanLyResponse> getDSDanhMuc(String tuKhoa, TrangThaiDanhMuc trangThai, Pageable pageable) {
        Specification<DanhMuc> spec = (root, query, cb) -> {
            // Để tránh N+1 query, fetch sẵn danh mục cha
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("danhMucCha", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(tuKhoa)) {
                Predicate tenPredicate = cb.like(cb.lower(root.get("tenDanhMuc")), "%" + tuKhoa.toLowerCase() + "%");
                // Chỉ tìm theo mã nếu từ khóa là một chuỗi số
                Predicate maPredicate = tuKhoa.matches("\\d+")
                        ? cb.equal(root.get("maDanhMuc"), Integer.parseInt(tuKhoa))
                        : cb.disjunction(); // Một predicate luôn false nếu không phải số
                predicates.add(cb.or(tenPredicate, maPredicate));
            }

            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<DanhMuc> danhMucPage = danhMucRepository.findAll(spec, pageable);

        List<DanhMucQuanLyResponse> responses = danhMucPage.getContent().stream()
                .map(this::mapToDanhMucQuanLyResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, danhMucPage.getNumber(), danhMucPage.getSize(), danhMucPage.getTotalElements(), danhMucPage.getTotalPages(), null);
    }

    private DanhMucQuanLyResponse mapToDanhMucQuanLyResponse(DanhMuc danhMuc) {
        return DanhMucQuanLyResponse.builder()
                .maDanhMuc(danhMuc.getMaDanhMuc())
                .tenDanhMuc(danhMuc.getTenDanhMuc())
                .tenDanhMucCha(danhMuc.getDanhMucCha() != null ? danhMuc.getDanhMucCha().getTenDanhMuc() : null)
                .trangThai(danhMuc.getTrangThai())
                .build();
    }

    @Override
    @Transactional
    public DanhMucQuanLyResponse themDanhMuc(ThemDanhMucRequest request) {
        DanhMuc danhMucCha = null;
        if (request.getMaDanhMucCha() != null) {
            danhMucCha = danhMucRepository.findById(request.getMaDanhMucCha())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha với mã: " + request.getMaDanhMucCha()));
        }

        String slug = generateUniqueSlug(request.getTenDanhMuc());

        DanhMuc danhMucMoi = DanhMuc.builder()
                .tenDanhMuc(request.getTenDanhMuc())
                .slug(slug)
                .danhMucCha(danhMucCha)
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : TrangThaiDanhMuc.HOAT_DONG)
                .build();

        DanhMuc savedDanhMuc = danhMucRepository.save(danhMucMoi);

        DanhMucQuanLyResponse response = mapToDanhMucQuanLyResponse(savedDanhMuc);
        response.setThongBao("Thêm danh mục thành công.");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietDanhMucQuanLyResponse getChiTietDanhMuc(Integer maDanhMuc) {
        DanhMuc danhMuc = danhMucRepository.findById(maDanhMuc)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với mã: " + maDanhMuc));

        return ChiTietDanhMucQuanLyResponse.builder()
                .maDanhMuc(danhMuc.getMaDanhMuc())
                .tenDanhMuc(danhMuc.getTenDanhMuc())
                .maDanhMucCha(danhMuc.getDanhMucCha() != null ? danhMuc.getDanhMucCha().getMaDanhMuc() : null)
                .trangThai(danhMuc.getTrangThai())
                .build();
    }

    @Override
    @Transactional
    public DanhMucQuanLyResponse capNhatDanhMuc(Integer maDanhMuc, CapNhatDanhMucRequest request) {
        DanhMuc danhMucToUpdate = danhMucRepository.findById(maDanhMuc)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với mã: " + maDanhMuc));

        // Cập nhật tên và slug nếu có thay đổi
        if (StringUtils.hasText(request.getTenDanhMuc()) && !request.getTenDanhMuc().equals(danhMucToUpdate.getTenDanhMuc())) {
            danhMucToUpdate.setTenDanhMuc(request.getTenDanhMuc());
            danhMucToUpdate.setSlug(generateUniqueSlug(request.getTenDanhMuc()));
        }

        // Cập nhật danh mục cha (phải thực hiện trước cập nhật trạng thái)
        if (request.isDanhMucChaUpdated()) {
            xuLyThayDoiDanhMucCha(danhMucToUpdate, request.getMaDanhMucCha());
        }

        // Cập nhật trạng thái
        if (request.getTrangThai() != null && request.getTrangThai() != danhMucToUpdate.getTrangThai()) {
            xuLyThayDoiTrangThai(danhMucToUpdate, request.getTrangThai());
        }

        // Không cần gọi save() vì entity đang ở trạng thái managed trong transaction
        DanhMucQuanLyResponse response = mapToDanhMucQuanLyResponse(danhMucToUpdate);
        response.setThongBao("Cập nhật danh mục thành công.");
        return response;
    }

    private void xuLyThayDoiDanhMucCha(DanhMuc danhMuc, Integer maDanhMucChaMoi) {
        Integer maDanhMucHienTai = danhMuc.getMaDanhMuc();
        Integer maDanhMucChaHienTai = Optional.ofNullable(danhMuc.getDanhMucCha()).map(DanhMuc::getMaDanhMuc).orElse(null);

        // Nếu không có thay đổi, không làm gì cả
        if (Objects.equals(maDanhMucChaMoi, maDanhMucChaHienTai)) {
            return;
        }

        // Ràng buộc: Không cho phép di chuyển một danh mục đang có danh mục con.
        // Điều này giúp đơn giản hóa logic và ngăn ngừa việc vô tình làm gãy cấu trúc cây.
        if (!danhMuc.getDanhMucCons().isEmpty()) {
            throw new IllegalArgumentException("Không thể di chuyển một danh mục đang có danh mục con.");
        }

        // Trường hợp 1: Biến thành danh mục gốc
        if (maDanhMucChaMoi == null) {
            danhMuc.setDanhMucCha(null);
        } else { // Trường hợp 2: Gán một danh mục cha mới
            // Ràng buộc: Không thể tự làm cha của chính mình.
            if (maDanhMucChaMoi.equals(maDanhMucHienTai)) {
                throw new IllegalArgumentException("Một danh mục không thể là danh mục cha của chính nó.");
            }

            DanhMuc danhMucChaMoiEntity = danhMucRepository.findById(maDanhMucChaMoi)
                    .orElseThrow(() -> new ResourceNotFoundException("Không thể tìm thấy danh mục cha với mã: " + maDanhMucChaMoi));

            // Ràng buộc: Không thể gán vào một danh mục cha đang không hoạt động.
            if (danhMucChaMoiEntity.getTrangThai() == TrangThaiDanhMuc.KHONG_HOAT_DONG) {
                throw new IllegalArgumentException("Không thể gán vào danh mục cha đang ở trạng thái không hoạt động.");
            }

            danhMuc.setDanhMucCha(danhMucChaMoiEntity);
        }
    }

    private void xuLyThayDoiTrangThai(DanhMuc danhMuc, TrangThaiDanhMuc trangThaiMoi) {
        // Ràng buộc: Khi kích hoạt một danh mục con, cha của nó phải đang hoạt động.
        if (trangThaiMoi == TrangThaiDanhMuc.HOAT_DONG && danhMuc.getDanhMucCha() != null) {
            if (danhMuc.getDanhMucCha().getTrangThai() == TrangThaiDanhMuc.KHONG_HOAT_DONG) {
                throw new IllegalArgumentException("Không thể kích hoạt danh mục con khi danh mục cha đang không hoạt động.");
            }
        }

        // Ràng buộc: Khi ngừng hoạt động một danh mục, tất cả con cháu của nó cũng bị ngừng theo.
        if (trangThaiMoi == TrangThaiDanhMuc.KHONG_HOAT_DONG) {
            // Lấy ID của danh mục này và tất cả các danh mục con cháu của nó.
            List<Integer> allRelatedCategoryIds = getAllSubCategoryIds(danhMuc.getMaDanhMuc());

            // Kiểm tra xem có sản phẩm nào đang "BÁN" trong cây danh mục này không.
            if (sanPhamRepository.existsByDanhMuc_MaDanhMucInAndTrangThai(allRelatedCategoryIds, TrangThaiSanPham.BAN)) {
                throw new IllegalArgumentException("Không thể ngừng hoạt động danh mục này vì vẫn còn sản phẩm đang bán trong đó hoặc trong các danh mục con.");
            }

            // Lấy tất cả các entity con cháu để cập nhật trạng thái.
            // Việc này hiệu quả hơn là gọi save() cho từng cái vì tất cả đều trong cùng 1 transaction.
            List<DanhMuc> descendants = danhMucRepository.findAllById(allRelatedCategoryIds);
            for (DanhMuc desc : descendants) {
                desc.setTrangThai(TrangThaiDanhMuc.KHONG_HOAT_DONG);
            }
        } else {
            // Nếu là kích hoạt, chỉ cần đặt trạng thái cho chính nó.
            danhMuc.setTrangThai(trangThaiMoi);
        }
    }

    /**
     * Tạo ra một slug duy nhất từ tên danh mục.
     * Ví dụ: "Áo Sơ Mi Nam" -> "ao-so-mi-nam".
     * Nếu slug đã tồn tại, nó sẽ thêm một hậu tố số, ví dụ: "ao-so-mi-nam-2".
     * @param name Tên danh mục.
     * @return Một chuỗi slug duy nhất.
     */
    private String generateUniqueSlug(String name) {
        // 1. Chuẩn hóa chuỗi: bỏ dấu, chuyển thành chữ thường
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("đ", "d");

        // 2. Thay thế các ký tự không phải chữ/số bằng dấu gạch ngang
        String baseSlug = normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");

        // 3. Kiểm tra và đảm bảo slug là duy nhất
        String finalSlug = baseSlug;
        int counter = 2;
        while (danhMucRepository.findBySlug(finalSlug).isPresent()) {
            finalSlug = baseSlug + "-" + counter++;
        }
        return finalSlug;
    }
}