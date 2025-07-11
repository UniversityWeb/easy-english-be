package com.universityweb.common.auth.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universityweb.common.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    @Schema(
            description = "Username of the user",
            example = "john",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String username;

    @Schema(
            description = "Fullname of the user",
            example = "john",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String fullName;

    @Schema(
            description = "Email of the user",
            example = "john@gmail.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email;

    @Schema(
            description = "Phone number of the user",
            example = "+84972640891",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String phoneNumber;

    @Schema(
            description = "Bio of the user",
            example = "A student.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String bio;

    @Schema(
            description = "Gender of the user",
            example = "MALE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    User.EGender gender;

    @Schema(
            description = "Day of birth",
            example = "2024-08-05",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDate dob;

    @Schema(
            description = "Role of the user",
            example = "",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    User.ERole role;

    @Schema(
            description = "Created date",
            example = "2024-08-05T13:47:06.794Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDateTime createdAt;

    String avatarPath;

    String preferredSkill;

    String learningGoal;

    User.ECurrentLevel currentLevel;

    @JsonIgnore
    String settings = "";

    @JsonGetter("settings")
    public SettingsDTO getSettingsDTO() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SettingsDTO settings = objectMapper.readValue(this.settings, SettingsDTO.class);
            return settings;
        } catch (Exception e) {
            return null;
        }
    }

    @JsonGetter("age")
    public int calculateAge() {
        if (dob == null) {
            return 0;
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
