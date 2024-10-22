package com.universityweb.testresult.request;

import com.universityweb.testresult.entity.TestResult;

import java.time.LocalDateTime;

public record AddTestResultRequest(
        TestResult.EStatus status,
        Integer takingDuration,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String username,
        Long testId
) {}
