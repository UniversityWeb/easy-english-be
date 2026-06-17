package com.universityweb.common.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token must not be blank")
    String refreshTokenStr
) {}
