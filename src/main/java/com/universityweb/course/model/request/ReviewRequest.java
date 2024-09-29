package com.universityweb.course.model.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ReviewRequest {
    private int id;
    private int courseId;
    private int rating;
    private String comment;
    private String user;
}
