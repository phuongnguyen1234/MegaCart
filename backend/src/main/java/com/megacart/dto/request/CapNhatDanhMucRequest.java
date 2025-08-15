package com.megacart.dto.request;

import com.megacart.enumeration.TrangThaiDanhMuc;
import lombok.Data;

@Data
public class CapNhatDanhMucRequest {
    private String tenDanhMuc;
    private Integer maDanhMucCha; // Có thể là null
    private TrangThaiDanhMuc trangThai;

    // Thêm trường này để xác định rõ ý định cập nhật danh mục cha.
    // Frontend cần gửi `true` khi người dùng thực sự thay đổi lựa chọn danh mục cha.
    private boolean isDanhMucChaUpdated = false;
}