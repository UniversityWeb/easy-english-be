package com.universityweb.testservice.testpart.dto;

import com.universityweb.questiongroup.dto.QuestionGroupDTO;

import java.util.List;

public record TestPartDTO(
        Long id,
        String title,
        String readingPassage,
        Integer ordinalNumber,
        Long testId,
        List<QuestionGroupDTO> questionGroups
) {}
