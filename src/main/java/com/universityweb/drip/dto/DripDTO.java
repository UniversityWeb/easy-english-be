package com.universityweb.drip.dto;

import com.universityweb.drip.Drip;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DripDTO {
    Long id;
    Long prevId;
    Drip.ESourceType prevType;
    Long nextId;
    Drip.ESourceType nextType;
    Boolean requiredCompletion;
    Long courseId;
}
