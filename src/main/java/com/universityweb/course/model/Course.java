package com.universityweb.course.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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
    private String title;
    private String imageUrl;
    private int duration;
    private String description;
    private double rating;
    private int ratingCount;
    private int countView;
    @Column(name = "publish")
    private Boolean isPublish;
    private String createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "active")
    private Boolean isActive;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FAQ> faqs;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    private Price price;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToMany
    @JoinTable(
            name = "course_category",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    public void updateRating() {
        if (reviews != null && !reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            this.rating = averageRating;
            this.ratingCount = reviews.size();
        } else {
            this.rating = 0.0;
        }
    }
}
