package com.universityweb.course.entity;

import com.universityweb.bundle.Bundle;
import com.universityweb.category.entity.Category;
import com.universityweb.common.auth.entity.User;
import com.universityweb.drip.Drip;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.faq.entity.FAQ;
import com.universityweb.favourite.entity.Favourite;
import com.universityweb.level.entity.Level;
import com.universityweb.price.entity.Price;
import com.universityweb.review.entity.Review;
import com.universityweb.section.entity.Section;
import com.universityweb.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToOne
    @JoinColumn(name = "username")
    User owner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    Price price;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    Topic topic;

    @ManyToOne
    @JoinColumn(name = "level_id")
    Level level;

    @ManyToMany(mappedBy = "courses")
    private Set<Bundle> bundles = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<FAQ> faqs;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Review> reviews;

    @ManyToMany
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<Category> categories;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Drip> drips;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Favourite> favourites;

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
