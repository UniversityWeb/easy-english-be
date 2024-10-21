package com.universityweb.review.response;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private double rating;
    private String comment;
    private String owner;
}
