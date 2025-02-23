package com.universityweb.courseservice.lesson.entity;

import com.universityweb.lesson.customenum.LessonType;
import com.universityweb.section.entity.Section;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted = false")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String title;

    @Enumerated(EnumType.STRING)
    private LessonType type;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String contentUrl;
    @Column(columnDefinition = "TEXT")
    private String description;
    private int duration;
    @Column(name = "preview")
    private Boolean isPreview;
    private LocalDate startDate;
    private LocalTime startTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
}
