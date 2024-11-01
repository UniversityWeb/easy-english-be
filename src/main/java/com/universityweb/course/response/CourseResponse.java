package com.universityweb.course.response;

import com.universityweb.category.response.CategoryResponse;
import com.universityweb.level.response.LevelResponse;
import com.universityweb.price.response.PriceResponse;
import com.universityweb.topic.response.TopicResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String category;
    private String imagePreview;
    private String videoPreview;
    private String descriptionPreview;
    private String description;
    private int duration;
    private int countView;
    private Long countStudent;
    private Long countSection;
    private Boolean isPublish;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Boolean isActive;
    private String notice;

    private double progress;
    private double rating;
    private Long ratingCount;

    private String ownerUsername;
    private PriceResponse price;
    private TopicResponse topic;
    private LevelResponse level;

    private List<CategoryResponse> categories;
}
