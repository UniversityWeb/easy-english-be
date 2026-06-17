package com.universityweb.section.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@Table(name = "sections")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "status != 'DELETED'")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @Column(name = "title")
    private String title;

    @CreationTimestamp
    @Column(name = "created_at")
    private String createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "course_id")
    @JsonBackReference
    private Long courseId;

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
        DISPLAY,
        HIDE,
        DELETED
    }
}
