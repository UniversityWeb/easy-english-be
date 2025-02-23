package com.universityweb.courseservice.section.dto;

import com.universityweb.section.entity.Section;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectionDTO {
    Long id;
    Section.EStatus status;
    String title;
    String createdAt;
    String updatedAt;
    Long courseId;
}
