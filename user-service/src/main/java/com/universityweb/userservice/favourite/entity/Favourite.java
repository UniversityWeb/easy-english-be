package com.universityweb.userservice.favourite.entity;

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

    @ManyToOne
    @JoinColumn(name = "username")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
