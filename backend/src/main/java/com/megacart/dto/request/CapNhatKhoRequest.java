package com.megacart.dto.request;

import com.megacart.enumeration.HinhThucCapNhatKho;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CapNhatKhoRequest {
    @NotNull(message = "Hình thức cập nhật không được để trống")
    private HinhThucCapNhatKho hinhThuc;

    @NotNull(message = "Số lượng cập nhật không được để trống")
    private Integer soLuong;

    @Size(max = 255, message = "Nội dung cập nhật không được vượt quá 255 ký tự")
    private String noiDung;
}
