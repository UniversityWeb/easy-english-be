package com.universityweb.review.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private double rating;
    private String comment;
    private Long courseId;
    private String owner;
    private Long parentReviewId;
    private List<ReviewDTO> childReviews;
}
