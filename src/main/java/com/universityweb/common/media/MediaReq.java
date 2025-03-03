package com.universityweb.common.media;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaReq {
    String base64Str;
}
