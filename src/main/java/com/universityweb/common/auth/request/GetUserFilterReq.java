package com.universityweb.common.auth.request;

import com.universityweb.common.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserFilterReq {
    @Schema(description = "The status of the user. Can be NULL for all statuses.", example = "ACTIVE")
    User.EStatus status;

    @Schema(example = "2024-12-31T23:59:59")
    LocalDate dob;

    @Schema(example = "2024-01-01T00:00:00")
    LocalDateTime createdAt;

    @Schema(description = "Full name filter for the user.", example = "John Doe")
    String fullName;

    @Schema(description = "Email filter for the user.", example = "johndoe@example.com")
    String email;

    @Schema(description = "The page number to fetch.", example = "0", allowableValues = {"0", "1", "2", "3"})
    int page = 0;

    @Schema(description = "The size of the page to fetch.", example = "10", allowableValues = {"10", "20", "50"})
    int size = 10;
}
