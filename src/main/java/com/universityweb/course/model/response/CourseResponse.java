package com.universityweb.course.model.response;

import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private List<Long> categoryIds;
    private Long levelId;
    private Long topicId;
    private String title;
    private String category;
    //private String level;
    private String imageUrl;
    private int duration;
    private String description;
    private double rating;
    private int ratingCount;
    private Boolean isPublish;
    //private String createdBy;
    private String createdAt;
    private Boolean isActive;
}
