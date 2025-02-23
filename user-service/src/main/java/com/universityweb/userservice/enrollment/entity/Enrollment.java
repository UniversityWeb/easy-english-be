package com.universityweb.userservice.enrollment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @ManyToOne
    @JoinColumn(name = "username")
    @JsonBackReference
    User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    Course course;

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
