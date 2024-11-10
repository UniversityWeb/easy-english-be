package com.universityweb.course.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private String username;
    private Long favoriteId;
    private Long id;
    private String title;
    private Long levelId;
    private Long topicId;
    private String imagePreview;
    private String videoPreview;
    private String descriptionPreview;
    private String description;
    private int duration;
    private int countView;
    private Boolean isPublish;
    private String createdAt;
    private Boolean isActive;
    private String notice;
    private String ownerUsername;
    private BigDecimal price;
    private Double rating;
    private List<Long> categoryIds;

    private int pageNumber = 0;
    private int size = 8;
}
