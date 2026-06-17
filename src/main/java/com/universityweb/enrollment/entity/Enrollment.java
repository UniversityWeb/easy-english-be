package com.universityweb.enrollment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "status != 'CANCELLED'")
public class Enrollment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    int progress;

    @Enumerated(EnumType.STRING)
    EStatus status;

    @Enumerated(EnumType.STRING)
    EType type;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "last_accessed")
    LocalDateTime lastAccessed;

    @Column(name = "username")
    @JsonBackReference
    String username;

    @Column(name = "course_id")
    @JsonBackReference
    Long courseId;

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

    public enum EStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED,
    }

    public enum EType {
        FREE,
        PAID,
        SCHOLARSHIP
    }
}
