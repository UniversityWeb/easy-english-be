package com.universityweb.test.dto;

import com.universityweb.test.entity.Test;
import com.universityweb.testpart.dto.TestPartDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestDTO {
    Long id;
    Test.EType type;
    Test.EStatus status;
    String title;
    String description;
    Integer ordinalNumber;
    Integer durationInMilis;
    Double passingGrade;
    LocalDateTime createdAt;
    String audioPath;
    List<TestPartDTO> parts;
    Long sectionId;
    Long courseId;

    Boolean isDone;

    boolean isLocked;
}
