package com.megacart.repository;

import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
     /**
     * Tìm tất cả các danh mục theo trạng thái.
     * @param trangThai Trạng thái cần tìm (ví dụ: HOAT_DONG).
     * @return Danh sách các danh mục khớp với trạng thái.
     */
    List<DanhMuc> findAllByTrangThai(TrangThaiDanhMuc trangThai);

    /**
     * Tìm một danh mục bằng slug của nó.
     * @param slug Slug duy nhất của danh mục.
     * @return Optional chứa danh mục nếu tìm thấy.
     */
    Optional<DanhMuc> findBySlug(String slug);

    /**
     * Tìm tất cả các danh mục gốc (không có danh mục cha) theo trạng thái.
     * @param trangThai Trạng thái của danh mục (ví dụ: HOAT_DONG).
     * @return Danh sách các danh mục gốc.
     */
    List<DanhMuc> findByDanhMucChaIsNullAndTrangThai(TrangThaiDanhMuc trangThai);
}