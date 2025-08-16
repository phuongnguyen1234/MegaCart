package com.megacart.service;

import com.megacart.dto.request.CapNhatDanhMucRequest;
import com.megacart.dto.response.ChiTietDanhMucQuanLyResponse;
import com.megacart.dto.request.ThemDanhMucRequest;
import com.megacart.dto.response.DanhMucQuanLyResponse;
import com.megacart.dto.response.DanhMucOptionResponse;
import com.megacart.enumeration.TrangThaiDanhMuc;
import com.megacart.dto.response.DanhMucMenuItemResponse;
import com.megacart.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DanhMucService {
    /**
     * Lấy danh sách các danh mục được sắp xếp theo cấu trúc cây để hiển thị trên menu.
     * Chỉ bao gồm các danh mục có trạng thái HOAT_DONG.
     * @return Một danh sách các danh mục gốc, mỗi danh mục chứa các danh mục con của nó.
     */
    List<DanhMucMenuItemResponse> getMenuDanhMucs();

    /**
     * Lấy ID của một danh mục và tất cả các ID của danh mục con cháu của nó.
     * @param parentId ID của danh mục cha.
     * @return Một danh sách chứa ID của danh mục cha và tất cả các danh mục con.
     */
    List<Integer> getAllSubCategoryIds(Integer parentId);

    /**
     * Lấy danh sách tất cả các danh mục đang hoạt động dưới dạng phẳng để hiển thị trong combobox lọc.
     * @return Một danh sách các tùy chọn danh mục.
     */
    List<DanhMucOptionResponse> getDanhMucOptionsForFilter();

    PagedResponse<DanhMucQuanLyResponse> getDSDanhMuc(String tuKhoa, TrangThaiDanhMuc trangThai, Pageable pageable);

    DanhMucQuanLyResponse themDanhMuc(ThemDanhMucRequest request);

    ChiTietDanhMucQuanLyResponse getChiTietDanhMuc(Integer maDanhMuc);

    DanhMucQuanLyResponse capNhatDanhMuc(Integer maDanhMuc, CapNhatDanhMucRequest request);
}