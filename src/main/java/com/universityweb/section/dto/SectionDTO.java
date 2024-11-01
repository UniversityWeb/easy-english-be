package com.universityweb.section.dto;

import com.universityweb.section.entity.Section;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectionDTO {
    private Long id;
    private Section.EStatus status;
    private String title;
    private String createdAt;
    private String updatedAt;
    private int ordinalNumber;
    private Long courseId;
}
