package com.universityweb.common.auth.request;

import com.universityweb.common.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserFilterReq {

    @Schema(
            description = "The status of the user. Can be NULL for all statuses.",
            example = "ACTIVE"
    )
    User.EStatus status;

    @Schema(
            description = "Full name filter for the user. Case-insensitive.",
            example = "John Doe"
    )
    String fullName;

    @Schema(
            description = "Email filter for the user. Case-insensitive.",
            example = "johndoe@example.com"
    )
    String email;

    @Schema(
            description = "Phone number filter for the user.",
            example = "+1234567890"
    )
    String phoneNumber;

    @Schema(
            description = "Gender filter for the user.",
            example = "MALE"
    )
    User.EGender gender;

    @Schema(
            description = "Role filter for the user. (TEACHER or STUDENT)",
            example = "TEACHER"
    )
    User.ERole role;

    @Schema(
            description = "The page number to fetch. Defaults to 0 (first page).",
            example = "0"
    )
    int page = 0;

    @Schema(
            description = "The size of the page to fetch. Defaults to 10.",
            example = "10"
    )
    int size = 10;
}
