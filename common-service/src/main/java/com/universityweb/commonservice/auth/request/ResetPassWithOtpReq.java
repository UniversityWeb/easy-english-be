package com.universityweb.common.auth.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPassWithOtpReq {
    String email;
    String otp;
    String newPassword;
}
