package com.universityweb.faq.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faqs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

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
}
