package com.universityweb.category.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    private Long id;
    private String name;
}
