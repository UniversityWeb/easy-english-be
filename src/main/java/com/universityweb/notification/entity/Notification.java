package com.universityweb.notification.entity;

import com.universityweb.common.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "is_deleted = false")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String previewImage;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    private boolean read;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
