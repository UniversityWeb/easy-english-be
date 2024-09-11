package com.universityweb.common.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(
                description = "Message",
                example = "Login successfully",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,

        @Schema(
                description = "Type of the token",
                example = "john",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tokenType,

        @Schema(
                description = "Token string",
                example = "qwerqwr234asdgasg...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tokenStr
) {}
