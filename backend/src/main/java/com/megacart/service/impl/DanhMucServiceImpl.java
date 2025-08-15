package com.megacart.service.impl;

import com.megacart.dto.response.DanhMucMenuItemResponse;
import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.model.DanhMuc;
import com.megacart.repository.DanhMucRepository;
import com.megacart.service.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DanhMucServiceImpl implements DanhMucService {

    private final DanhMucRepository danhMucRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DanhMucMenuItemResponse> getMenuDanhMucs() {
        final int SUB_CATEGORY_LIMIT = 12;

        // 1. Lấy tất cả danh mục đang hoạt động
        List<DanhMuc> allActiveCategories = danhMucRepository.findAllByTrangThai(TrangThaiDanhMuc.HOAT_DONG);

        // 2. Nhóm các danh mục con theo ID của cha để tra cứu hiệu quả
        Map<Integer, List<DanhMuc>> childrenByParentId = allActiveCategories.stream()
                .filter(dm -> dm.getDanhMucCha() != null)
                .collect(Collectors.groupingBy(dm -> dm.getDanhMucCha().getMaDanhMuc()));

        // 3. Lấy ra các danh mục gốc (không có cha)
        List<DanhMuc> rootCategoryEntities = allActiveCategories.stream()
                .filter(dm -> dm.getDanhMucCha() == null)
                .toList();

        // 4. Xây dựng cây DTO từ các danh mục gốc, áp dụng giới hạn cho các danh mục con
        return rootCategoryEntities.stream()
                .map(rootEntity -> {
                    // Lấy danh sách con đầy đủ của danh mục gốc này
                    List<DanhMuc> children = childrenByParentId.getOrDefault(rootEntity.getMaDanhMuc(), Collections.emptyList());

                    // Giới hạn danh sách con để trả về cho menu
                    List<DanhMucMenuItemResponse> limitedChildrenDTOs = children.stream()
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
}