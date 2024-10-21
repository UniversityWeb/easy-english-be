package com.universityweb.section.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequest {
    private Long id;
    private Long courseId;
    private String title;
    private String createdBy;
    private String createdAt;
    private int pageNumber = 0;
    private int size = 10;
}
