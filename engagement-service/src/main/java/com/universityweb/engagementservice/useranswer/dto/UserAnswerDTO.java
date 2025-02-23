package com.universityweb.engagementservice.useranswer.dto;

import com.universityweb.testquestion.dto.TestQuestionDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerDTO {
    Long id;
    Integer ordinalNumber;
    List<String> answers;
    Boolean isCorrect;
    Long testQuestionId;
    Long testResultId;
    TestQuestionDTO testQuestion;
}
