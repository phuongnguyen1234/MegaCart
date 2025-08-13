package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Không hiển thị danh sách con nếu nó rỗng hoặc null
public class DanhMucMenuItemResponse {
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private String slug;
    private List<DanhMucMenuItemResponse> danhMucCons;
}