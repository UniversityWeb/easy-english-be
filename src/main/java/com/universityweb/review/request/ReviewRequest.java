package com.universityweb.review.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String response;
}
