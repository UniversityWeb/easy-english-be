package com.universityweb.common.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SessionResponse {
    @Schema(description = "Session ID", example = "1")
    private Long id;

    @Schema(description = "Device Info", example = "Mozilla/5.0 ...")
    private String deviceInfo;

    @Schema(description = "IP Address", example = "127.0.0.1")
    private String ipAddress;

    @Schema(description = "Login Location", example = "Unknown Location")
    private String loginLocation;

    @Schema(description = "Token Expiration Time")
    private LocalDateTime expiryDate;

    @Schema(description = "Is current session")
    private boolean isCurrent;
}
