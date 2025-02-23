package com.universityweb.userservice.user.dto;

import com.universityweb.common.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserForAdminDTO {

    @Schema(description = "The unique username of the user.", example = "john_doe")
    String username;

    @Schema(description = "Password of the user. Will not be shown for security reasons.", example = "null")
    String password;

    @Schema(description = "Full name of the user.", example = "John Doe")
    String fullName;

    @Schema(description = "Email address of the user.", example = "john.doe@example.com")
    String email;

    @Schema(description = "Phone number of the user.", example = "+123456789")
    String phoneNumber;

    @Schema(description = "Short biography or description of the user.", example = "A passionate teacher and mentor.")
    String bio;

    @Schema(description = "Gender of the user.", example = "MALE")
    User.EGender gender;

    @Schema(description = "Date of birth of the user.", example = "1990-01-01")
    LocalDate dob;

    @Schema(description = "Role assigned to the user.", example = "ADMIN")
    User.ERole role;

    @Schema(description = "The timestamp of when the user account was created.", example = "2024-01-01T10:00:00")
    LocalDateTime createdAt;

    @Schema(description = "Current status of the user account.", example = "ACTIVE")
    User.EStatus status;

    @Schema(description = "Path to the avatar image of the user.", example = "/images/avatars/john_doe.png")
    String avatarPath;
}
