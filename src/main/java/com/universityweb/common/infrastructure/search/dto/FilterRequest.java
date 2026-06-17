package com.universityweb.common.infrastructure.search.dto;

import com.universityweb.common.infrastructure.search.operator.FilterOperator;
import lombok.Data;

@Data
public class FilterRequest {
    private String field;
    private FilterOperator operator;
    private Object value;
}
