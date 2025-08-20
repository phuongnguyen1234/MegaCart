package com.megacart.dto.request;

import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CapNhatDonHangRequest {
    private TrangThaiDonHang trangThai;
    private TrangThaiThanhToan trangThaiThanhToan;

    // Validation sẽ được xử lý ở tầng service để linh hoạt hơn cho thao tác PATCH
    private LocalDateTime duKienGiaoHang;

    private String ghiChu;
}