package com.universityweb.testquestion.dto;

import com.universityweb.testquestion.entity.TestQuestion;

import java.util.List;

public record TestQuestionDTO(
        Long id,
        TestQuestion.EType type,
        Integer ordinalNumber,
        String title,
        String description,
        String audioPath,
        String imagePath,
        List<String> options,
        List<String> correctAnswers,
        Long questionGroupId
) {}
