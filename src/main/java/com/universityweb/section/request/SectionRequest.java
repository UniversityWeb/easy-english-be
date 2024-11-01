package com.universityweb.section.request;

import com.universityweb.section.entity.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequest {
    private Long id;
    private Section.EStatus status;
    private String title;
    private String createdAt;
    private String updatedAt;
    private int ordinalNumber;
    private Long courseId;

    private int pageNumber = 0;
    private int size = 10;
}
