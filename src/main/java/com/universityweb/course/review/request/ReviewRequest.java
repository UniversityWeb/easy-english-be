package com.universityweb.course.review.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewRequest {
    private Long id;
    private Long courseId;
    private int rating;
    private String comment;
    private String user;
}