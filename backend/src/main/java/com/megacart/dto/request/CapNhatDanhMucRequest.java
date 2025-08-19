package com.megacart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megacart.enumeration.TrangThaiDanhMuc;
import lombok.Data;

@Data
public class CapNhatDanhMucRequest {
    private String tenDanhMuc;
    private Integer maDanhMucCha;
    private TrangThaiDanhMuc trangThai;

    // Sử dụng @JsonProperty để Jackson có thể nhận cả "isDanhMucChaUpdated" từ JSON
    // và ánh xạ nó vào trường danhMucChaUpdated.
    @JsonProperty("isDanhMucChaUpdated")
    private boolean danhMucChaUpdated;
}