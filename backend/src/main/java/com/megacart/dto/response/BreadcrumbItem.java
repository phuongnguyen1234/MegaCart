package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Sẽ không hiển thị trường 'to' nếu nó là null
public class BreadcrumbItem {
    private String text;
    private String to; // URL/link cho mỗi mục breadcrumb, khớp với prop 'to' của Vue component
}