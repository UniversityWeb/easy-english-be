package com.universityweb.enrollment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Enrollment implements Serializable {
    @Id
    private Long id;

    private double progress;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @Enumerated(EnumType.STRING)
    private EType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @ManyToOne
    @JoinColumn(name = "username")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

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
