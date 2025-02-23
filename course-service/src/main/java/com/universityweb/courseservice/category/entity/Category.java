package com.universityweb.courseservice.category.entity;

import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
@Where(clause = "is_deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isDeleted = false;

    @ManyToMany(mappedBy = "categories")
    List<Course> courses;

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
