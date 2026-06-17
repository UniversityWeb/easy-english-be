package com.universityweb.common.infrastructure.search.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class SortRequest {
    private String field;
    private Sort.Direction direction = Sort.Direction.ASC;
}
