package com.universityweb.course.model.response;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private int id;
    private double rating;
    private String comment;
    private String owner;
}
