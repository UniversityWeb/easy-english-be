package com.universityweb.section.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {
    private Long courseId;
    private Long id;
    private String title;
    private String createdBy;
    private String createdAt;
    private int ordinalNumber;
}
