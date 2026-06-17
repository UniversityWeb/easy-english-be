package com.universityweb.common.infrastructure.search.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRequest {
    private int page = 0;
    private int size = 20;
    private List<SortRequest> sort = new ArrayList<>();
    private List<FilterRequest> filters = new ArrayList<>();
}
