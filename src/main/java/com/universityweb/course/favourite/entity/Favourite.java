package com.universityweb.course.favourite.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.common.auth.entity.User;
import com.universityweb.course.common.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "favourites")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Favourite  {
    @Id
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "username")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

}
