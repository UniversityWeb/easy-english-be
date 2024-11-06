package com.universityweb.testresult.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class SubmitTestRequest {
    Long testId;
    Integer takingDuration;
    LocalDateTime startedAt;
    LocalDateTime finishedAt;
    List<UserAnswerDTO> userAnswers;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @FieldDefaults(level= AccessLevel.PRIVATE)
    public static class UserAnswerDTO {
        List<String> answers;
        Long testQuestionId;
    }
}
