package com.universityweb.common.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileWithOTPRequest extends UpdateProfileRequest {
        @Schema(
                description = "One-Time Password (OTP) for profile update verification",
                example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "OTP is required")
        private String otp;
}