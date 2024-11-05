package com.universityweb.drip;

import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "course_drips")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Drip implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prev_id")
    private Long prevId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_type")
    private ESourceType prevType;

    @Column(name = "next_id")
    private Long nextId;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_type")
    private ESourceType nextType;

    @Column(name = "required_completion")
    private Boolean requiredCompletion;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public enum ESourceType {
        LESSON,
        TEST
    }
}
