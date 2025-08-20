package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MucTieuDoanhThuResponse {
    private long mucTieu;
    private long thucTe;
}
