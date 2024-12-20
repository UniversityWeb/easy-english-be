package com.universityweb.course.entity;

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
import java.util.List;

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

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (status == null) {
            status = EStatus.DRAFT;
        }
    }
}
