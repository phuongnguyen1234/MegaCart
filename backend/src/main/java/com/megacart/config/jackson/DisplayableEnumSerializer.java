package com.megacart.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom serializer cho các Enum implement DisplayableEnum.
 * Chuyển đổi một Enum thành một đối tượng JSON.
 * Ví dụ: TrangThaiDonHang.DANG_GIAO -> { "value": "DANG_GIAO", "label": "Đang giao" }
 */
public class DisplayableEnumSerializer extends JsonSerializer<DisplayableEnum> {

    @Override
    public void serialize(DisplayableEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // Ép kiểu về Enum để có thể lấy được name() (tên hằng số)
        gen.writeStartObject();
        gen.writeStringField("value", ((Enum<?>) value).name());
        gen.writeStringField("label", value.getTenHienThi());
        gen.writeEndObject();
    }
}