package com.universityweb.level.entity;

import com.universityweb.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "levels")
@Where(clause = "is_deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String fromLevel;
    String toLevel;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    Topic topic;

    @PrePersist
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
