package com.universityweb.courseservice.section.request;

import com.universityweb.section.entity.Section;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectionRequest {
    private Long id;
    private Section.EStatus status;
    private String title;
    private String createdAt;
    private String updatedAt;
    private Long courseId;

    private int pageNumber = 0;
    private int size = 10;

}
