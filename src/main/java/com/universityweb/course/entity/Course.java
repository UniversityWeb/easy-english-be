package com.universityweb.course.entity;

import com.universityweb.category.entity.Category;
import com.universityweb.common.auth.entity.User;
import com.universityweb.level.entity.Level;
import com.universityweb.price.entity.Price;
import com.universityweb.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Table(name = "courses")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "status != 'DELETED'")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    String title;

    @Column(name = "image_preview")
    String imagePreview;

    @Column(name = "video_preview")
    String videoPreview;

    @Column(name = "description_preview", length = 1000)
    String descriptionPreview;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "duration")
    int duration;

    @Column(name = "count_view")
    int countView;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "notice", columnDefinition = "TEXT")
    String notice;

    @Enumerated(EnumType.STRING)
    EStatus status;

    @Enumerated(EnumType.STRING)
    User.ECurrentLevel prerequisiteLevel;

    @Enumerated(EnumType.STRING)
    EDifficulty difficulty;

    @Column(name = "owner_username")
    String ownerUsername;

    @Column(name = "price_id")
    Long priceId;

    @Column(name = "topic_id")
    Long topicId;

    @Column(name = "level_id")
    Long levelId;

    @ElementCollection
    @CollectionTable(name = "course_category", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "category_id")
    List<Long> categoryIds = new ArrayList<>();

    @Transient
    public User getOwner() {
        if (ownerUsername == null) {
            return null;
        }
        return User.builder().username(ownerUsername).build();
    }

    public void setOwner(User owner) {
        this.ownerUsername = owner == null ? null : owner.getUsername();
    }

    @Transient
    public Price getPrice() {
        if (priceId == null) {
            return null;
        }
        return Price.builder().id(priceId).build();
    }

    public void setPrice(Price price) {
        this.priceId = price == null ? null : price.getId();
    }

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

    @Transient
    public Level getLevel() {
        if (levelId == null) {
            return null;
        }
        return Level.builder().id(levelId).build();
    }

    public void setLevel(Level level) {
        this.levelId = level == null ? null : level.getId();
    }

    @Transient
    public List<Category> getCategories() {
        return categoryIds == null
                ? new ArrayList<>()
                : categoryIds.stream().map(id -> Category.builder().id(id).build()).collect(Collectors.toList());
    }

    public void setCategories(List<Category> categories) {
        this.categoryIds = categories == null
                ? new ArrayList<>()
                : categories.stream().map(Category::getId).collect(Collectors.toList());
    }

    public enum EStatus {
        PUBLISHED,
        REJECTED,
        PENDING_APPROVAL,
        DRAFT,
        DELETED,
    }

    public enum EDifficulty {
        BEGINNER,
        ELEMENTARY,
        INTERMEDIATE,
        UPPER_INTERMEDIATE,
        ADVANCED;

        public static EDifficulty fromString(String label) {
            switch (label.trim().toUpperCase().replace("-", "_")) {
                case "BEGINNER":
                    return BEGINNER;
                case "ELEMENTARY":
                    return ELEMENTARY;
                case "INTERMEDIATE":
                    return INTERMEDIATE;
                case "UPPER_INTERMEDIATE":
                    return UPPER_INTERMEDIATE;
                case "ADVANCED":
                    return ADVANCED;
                default:
                    throw new IllegalArgumentException("Unknown difficulty level: " + label);
            }
        }

        public String toDisplayName() {
            switch (this) {
                case BEGINNER:
                    return "Beginner";
                case ELEMENTARY:
                    return "Elementary";
                case INTERMEDIATE:
                    return "Intermediate";
                case UPPER_INTERMEDIATE:
                    return "Upper-Intermediate";
                case ADVANCED:
                    return "Advanced";
                default:
                    return name();
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (status == null) {
            status = EStatus.DRAFT;
        }
    }
}
