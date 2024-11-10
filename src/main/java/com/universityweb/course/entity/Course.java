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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "courses")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "image_preview")
    private String imagePreview;

    @Column(name = "video_preview")
    private String videoPreview;

    @Column(name = "description_preview", length = 1000)
    private String descriptionPreview;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration")
    private int duration;

    @Column(name = "count_view")
    private int countView;

    @Column(name = "publish")
    private Boolean isPublish;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private Boolean isActive;

    @Column(name = "notice", columnDefinition = "TEXT")
    private String notice;

    @ManyToOne
    @JoinColumn(name = "username")
    private User owner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    private Price price;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FAQ> faqs;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Drip> drips;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favourite> favourites;
}
