package com.universityweb.engagementservice.review.entity;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Builder
@Table(name = "reviews")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;

    @Column(name = "rating")
    double rating;

    @Column(name = "comment")
    String comment;

    @Column(name = "response")
    String response;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
