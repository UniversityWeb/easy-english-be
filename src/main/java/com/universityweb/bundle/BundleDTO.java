package com.universityweb.bundle;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BundleDTO {
    Long id;
    String name;
    String imagePreview = "";
    String desc;
    BigDecimal price;
    List<Long> courseIds;
}
