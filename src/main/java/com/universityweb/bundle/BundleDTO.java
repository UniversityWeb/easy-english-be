package com.universityweb.bundle;

import com.universityweb.course.entity.Course;
import com.universityweb.price.entity.Price;
import com.universityweb.price.response.PriceResponse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String imagePreview;
    String desc;
    Boolean isDeleted;
    PriceResponse price;
    List<Long> courseIds;
}
