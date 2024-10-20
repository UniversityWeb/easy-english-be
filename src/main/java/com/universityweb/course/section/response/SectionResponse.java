package com.universityweb.course.section.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {
    private Long id;
    private String title;
    private String createdBy;
    private String createdAt;

}
