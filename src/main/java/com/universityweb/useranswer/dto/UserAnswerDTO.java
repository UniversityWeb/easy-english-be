package com.universityweb.useranswer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAnswerDTO {
    private Long id;
    private List<String> answers;
    private Boolean isCorrect;
    private Long testQuestionId;
    private Long testResultId;
}
