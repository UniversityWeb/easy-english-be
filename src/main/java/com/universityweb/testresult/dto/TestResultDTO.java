package com.universityweb.testresult.dto;

import com.universityweb.test.dto.TestDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestResultDTO {
    Long id;
    String result;
    Double correctPercent;
    TestResult.EStatus status;
    Integer takingDuration;
    LocalDateTime startedAt;
    LocalDateTime finishedAt;
    String username;
    Long testId;
    TestDTO test;
    Long courseId;
    List<UserAnswerDTO> userAnswers;
}
