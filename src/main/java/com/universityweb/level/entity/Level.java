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

    @Column(name = "topic_id", nullable = false)
    Long topicId;

    @Transient
    public Topic getTopic() {
        if (topicId == null) {
            return null;
        }
        return Topic.builder().id(topicId).build();
    }

    public void setTopic(Topic topic) {
        this.topicId = topic == null ? null : topic.getId();
    }

    @PrePersist
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
