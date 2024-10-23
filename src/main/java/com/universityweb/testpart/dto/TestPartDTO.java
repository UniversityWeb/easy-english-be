package com.universityweb.testpart.dto;

import com.universityweb.testquestion.dto.TestQuestionDTO;

import java.util.List;

public record TestPartDTO(
        Long id,
        String title,
        Integer ordinalNumber,
        Long testId,
        List<TestQuestionDTO> questions
) {}
