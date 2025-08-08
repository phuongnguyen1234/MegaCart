package com.megacart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongTinKhachHangResponse {
    private String tenKhachHang;
    private String email; // Email thường không cho phép thay đổi, chỉ hiển thị
    private String soDienThoai;
    private String diaChi;
}