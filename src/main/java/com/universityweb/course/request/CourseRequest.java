package com.universityweb.course.request;

import com.universityweb.course.entity.Course;
import com.universityweb.price.response.PriceResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    String username;
    Long favoriteId;
    Long id;
    String title;
    Long levelId;
    Long topicId;
    String imagePreview;
    String videoPreview;
    String descriptionPreview;
    String description;
    int duration;
    int countView;
    String notice;
    Course.EStatus status;

    String ownerUsername;
    BigDecimal price;
    Double rating;
    List<Long> categoryIds;

    int pageNumber = 0;
    int size = 8;
}
