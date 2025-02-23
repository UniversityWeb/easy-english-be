package com.universityweb.courseservice.course.response;

import com.universityweb.category.response.CategoryResponse;
import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.course.entity.Course;
import com.universityweb.level.response.LevelResponse;
import com.universityweb.price.response.PriceResponse;
import com.universityweb.topic.response.TopicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    Long id;
    String title;
    String category;
    String imagePreview;
    String videoPreview;
    String descriptionPreview;
    String description;
    int duration;
    int countView;
    Long countSection;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String notice;
    Course.EStatus status;
    UserDTO owner;

    Long countStudent;
    int progress;
    double rating;
    Long ratingCount;
    BigDecimal totalRevenue;

    String ownerUsername;
    PriceResponse price;
    TopicResponse topic;
    LevelResponse level;

    List<CategoryResponse> categories;

    // init - don't delete
    LocalDateTime updateAt;
}
