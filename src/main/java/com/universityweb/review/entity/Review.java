package com.universityweb.review.entity;

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

    @Column(name = "course_id")
    Long courseId;

    @Column(name = "rating")
    double rating;

    @Column(name = "comment")
    String comment;

    @Column(name = "response")
    String response;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "user_id")
    String userId;

    @Transient
    public Course getCourse() {
        if (courseId == null) {
            return null;
        }
        return Course.builder().id(courseId).build();
    }

    public void setCourse(Course course) {
        this.courseId = course == null ? null : course.getId();
    }

    @Transient
    public User getUser() {
        if (userId == null) {
            return null;
        }
        return User.builder().username(userId).build();
    }

    public void setUser(User user) {
        this.userId = user == null ? null : user.getUsername();
    }

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
