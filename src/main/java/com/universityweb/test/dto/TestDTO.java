package com.universityweb.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.universityweb.drip.dto.PrevDripDTO;
import com.universityweb.test.entity.Test;
import com.universityweb.testpart.dto.TestPartDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TestDTO {
    Long id;
    Test.EType type;
    Test.EStatus status;
    String title;
    String description;
    Integer durationInMilis;
    Double passingGrade;
    LocalDateTime createdAt;
    String audioPath;
    List<TestPartDTO> parts;
    Long sectionId;
    Long courseId;

    Boolean isDone;

    @JsonProperty("isLocked")
    boolean isLocked;

    Set<PrevDripDTO> prevDrips;
}
