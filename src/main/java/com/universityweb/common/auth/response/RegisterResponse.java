package com.universityweb.common.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.universityweb.common.auth.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.ResponseBody;

public record RegisterResponse(
        @Schema(
                description = "Message",
                example = "Register successfully",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,

        @Schema(
                description = "User information",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @ResponseBody
        @JsonProperty("user")
        UserDTO userDTO
) {}
