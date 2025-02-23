package com.universityweb.testservice.testquestion.dto;

import com.universityweb.testquestion.entity.TestQuestion;

import java.util.List;

public record TestQuestionDTO(
        Long id,
        TestQuestion.EType type,
        Integer ordinalNumber,
        String title,
        String description,
        List<String> options,
        List<String> correctAnswers,
        Long questionGroupId
) {}
