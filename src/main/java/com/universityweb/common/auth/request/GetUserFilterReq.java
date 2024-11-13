package com.universityweb.common.auth.request;

import com.universityweb.common.auth.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserFilterReq {
    User.EStatus status;
    LocalDate startDate;
    LocalDate endDate;
    String fullName;
    String email;

    int page = 0;
    int size = 10;
}
