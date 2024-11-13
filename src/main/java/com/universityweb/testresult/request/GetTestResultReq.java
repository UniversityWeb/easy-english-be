package com.universityweb.testresult.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTestResultReq {
    Long testId;
    int page = 0;
    int size = 8;
}
