package com.universityweb.common.auth.dto;

import com.universityweb.common.auth.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserForAdminDTO {
    String username;
    String password;
    String fullName;
    String email;
    String phoneNumber;
    String bio;
    User.EGender gender;
    LocalDate dob;
    User.ERole role;
    LocalDateTime createdAt;
    User.EStatus status;
    String avatarPath;
}
