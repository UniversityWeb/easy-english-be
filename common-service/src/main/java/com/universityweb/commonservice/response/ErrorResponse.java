package com.universityweb.commonservice.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp
) {}
