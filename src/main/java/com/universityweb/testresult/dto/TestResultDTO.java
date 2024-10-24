package com.universityweb.testresult.dto;

import com.universityweb.testresult.entity.TestResult;

import java.time.LocalDateTime;

public record TestResultDTO(
        Long id,
        String result,
        TestResult.EStatus status,
        Integer takingDuration,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String username,
        Long testId
) {}
