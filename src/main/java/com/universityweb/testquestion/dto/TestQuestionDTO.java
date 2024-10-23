package com.universityweb.testquestion.dto;

import com.universityweb.testquestion.entity.TestQuestion;

public record TestQuestionDTO(
        Long id,
        String answers,
        String answerKey,
        String imageUrl,
        TestQuestion.EType type,
        Long testPartId
) {}
