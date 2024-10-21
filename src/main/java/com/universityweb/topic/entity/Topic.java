package com.universityweb.topic.entity;

import com.universityweb.course.entity.Course;
import com.universityweb.level.entity.Level;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "topic")
    private List<Course> courses;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Level> levels;
}
