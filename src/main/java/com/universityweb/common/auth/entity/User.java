package com.universityweb.common.auth.entity;

import com.universityweb.bundle.Bundle;
import com.universityweb.cart.entity.Cart;
import com.universityweb.course.entity.Course;
import com.universityweb.message.Message;
import com.universityweb.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "status != 'DELETED'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails, Serializable {
    @Id
    String username;

    @Column(columnDefinition = "TEXT")
    String password;

    @Column(name = "full_name")
    String fullName;

    @Column(unique = true, nullable = false)
    String email;

    @Column(name = "phone_number", nullable = false)
    String phoneNumber;

    @Column(columnDefinition = "TEXT")
    String bio;

    @Enumerated(EnumType.STRING)
    EGender gender;

    @Column(name = "dob")
    LocalDate dob;

    @Enumerated(EnumType.STRING)
    ERole role;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "last_login", columnDefinition = "TIMESTAMP")
    LocalDateTime lastLogin ;

    @Enumerated(EnumType.STRING)
    EStatus status;

    @Column(name = "avatar_path", columnDefinition = "TEXT")
    String avatarPath;

    @Column(name = "settings", columnDefinition = "TEXT")
    String settings;

    String preferredSkill;

    String learningGoal;

    @Enumerated(EnumType.STRING)
    ECurrentLevel currentLevel;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Cart cart;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = true)
    Token token;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Course> courses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Review> reviews;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Message> sentMessages;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Message> receivedMessages;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Bundle> bundles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(getRole().name().toUpperCase()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum EGender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum ERole {
        ADMIN,
        TEACHER,
        STUDENT
    }

    public enum EStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }

    public enum ECurrentLevel {
        BEGINNER(0),
        ELEMENTARY(1),
        INTERMEDIATE(2),
        UPPER_INTERMEDIATE(3),
        ADVANCED(4);

        private final int value;

        ECurrentLevel(int levelCode) {
            this.value = levelCode;
        }

        public int getValue() {
            return value;
        }

        public static ECurrentLevel fromCode(int code) {
            for (ECurrentLevel level : values()) {
                if (level.getValue() == code) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Invalid level code: " + code);
        }
    }

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (gender == null) {
            gender = EGender.OTHER;
        }
        if (status == null) {
            status = EStatus.INACTIVE;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
