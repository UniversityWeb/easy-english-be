package com.universityweb.bundle;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import com.universityweb.price.entity.Price;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bundles")
@Where(clause = "is_deleted = false")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bundle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "title", nullable = false)
    String name;

    @Column(name = "image_preview")
    String imagePreview;

    @Column(columnDefinition = "TEXT")
    String desc;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isDeleted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    Price price;

    @ManyToOne
    @JoinColumn(name = "username")
    User owner;

    @ManyToMany
    @JoinTable(
            name = "bundle_course",
            joinColumns = @JoinColumn(name = "bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    @PrePersist
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
