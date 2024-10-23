package com.universityweb.testpart.request;

public record AddTestPartRequest(
        String title,
        Integer ordinalNumber,
        Long testId
) {}
