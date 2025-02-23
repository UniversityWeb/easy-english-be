package com.universityweb.engagementservice.review.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    Long id;
    Long courseId;
    int rating;
    String comment;
    String user;
    String response;
}
