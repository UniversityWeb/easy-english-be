package com.universityweb.review.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewRequest {
    private Long id;
    private Long courseId;
    private int rating;
    private String comment;
    private String user;
    private String parentReviewId;
}
