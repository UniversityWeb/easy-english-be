package com.universityweb.topic.entity;

import com.universityweb.course.entity.Course;
import com.universityweb.level.entity.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "topics")
@Where(clause = "is_deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isDeleted = false;

    @OneToMany(mappedBy = "topic")
    List<Course> courses;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    List<Level> levels;

    @PrePersist
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
