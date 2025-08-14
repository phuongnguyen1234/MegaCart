package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnumOptionResponse {
    private String value; // e.g., GIAO_HANG_TAN_NHA
    private String label; // e.g., Giao hàng tận nhà
}