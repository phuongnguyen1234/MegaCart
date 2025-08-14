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
        // 1. Lấy tất cả danh mục đang hoạt động từ CSDL trong một lần gọi để tối ưu hiệu năng
        List<DanhMuc> allActiveCategories = danhMucRepository.findAllByTrangThai(TrangThaiDanhMuc.HOAT_DONG);

        // 2. Chuyển đổi tất cả các entity sang DTO và tạo một map để truy cập nhanh với key là ID
        Map<Integer, DanhMucMenuItemResponse> categoryMap = allActiveCategories.stream()
                .map(dm -> DanhMucMenuItemResponse.builder().maDanhMuc(dm.getMaDanhMuc()).tenDanhMuc(dm.getTenDanhMuc()).slug(dm.getSlug()).build())
                .collect(Collectors.toMap(DanhMucMenuItemResponse::getMaDanhMuc, item -> item));

        // 3. Xây dựng cấu trúc cây
        List<DanhMucMenuItemResponse> rootCategories = new ArrayList<>();
        allActiveCategories.forEach(danhMuc -> {
            DanhMucMenuItemResponse currentItem = categoryMap.get(danhMuc.getMaDanhMuc());
            if (danhMuc.getDanhMucCha() == null) {
                // Nếu là danh mục gốc, thêm vào danh sách kết quả
                rootCategories.add(currentItem);
            } else {
                // Nếu là danh mục con, tìm cha của nó trong map và thêm vào danh sách con của cha
                DanhMucMenuItemResponse parentItem = categoryMap.get(danhMuc.getDanhMucCha().getMaDanhMuc());
                if (parentItem != null) {
                    if (parentItem.getDanhMucCons() == null) {
                        parentItem.setDanhMucCons(new ArrayList<>());
                    }
                    parentItem.getDanhMucCons().add(currentItem);
                }
            }
        });

        return rootCategories;
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
}