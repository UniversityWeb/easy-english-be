package com.universityweb.testresult.dto;

import com.universityweb.test.dto.TestDTO;
import com.universityweb.testresult.entity.TestResult;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestResultWithoutListDTO {
    Long id;
    String result;
    Double correctPercent;
    TestResult.EStatus status;
    Integer takingDuration;
    LocalDateTime startedAt;
    LocalDateTime finishedAt;
    String username;
    Long testId;
    Long courseId;
}
