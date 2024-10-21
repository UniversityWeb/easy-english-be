package com.universityweb.level.entity;

import com.universityweb.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "levels")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String fromLevel;
    private String toLevel;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

}
