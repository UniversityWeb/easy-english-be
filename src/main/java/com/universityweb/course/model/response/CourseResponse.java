package com.universityweb.course.model.response;

import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private int id;
    private String title;
    private String category;
    private String level;
    private String imageUrl;
    private int duration;
    private int price;
    private String description;
    private double rating;
    private int ratingCount;
    private Boolean isPublish;
    private String createdBy;
    private String createdAt;
}
