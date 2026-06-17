package com.universityweb.favourite.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;


@Entity
@Table(name = "favourites")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "is_deleted = false")
public class Favourite  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted;

    @Column(name = "username")
    @JsonBackReference
    private String username;

    @Column(name = "course_id")
    @JsonBackReference
    private Long courseId;

    @Transient
    public User getUser() {
        if (username == null) {
            return null;
        }
        return User.builder().username(username).build();
    }

    public void setUser(User user) {
        this.username = user == null ? null : user.getUsername();
    }

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

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
