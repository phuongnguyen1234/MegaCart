package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ bao gồm các trường không null trong JSON response
public class CapNhatHoSoResponse {
    private String message;
    private boolean emailUpdateInitiated; // true nếu việc đổi email đã bắt đầu
    private ThongTinKhachHangResponse thongTinCapNhat; // Trả về thông tin mới nếu có

    public static CapNhatHoSoResponse success(ThongTinKhachHangResponse data, String message) {
        return CapNhatHoSoResponse.builder().message(message).emailUpdateInitiated(false).thongTinCapNhat(data).build();
    }

    public static CapNhatHoSoResponse emailVerificationNeeded(String message) {
        return CapNhatHoSoResponse.builder().message(message).emailUpdateInitiated(true).build();
    }
}