package com.universityweb.message;

import com.universityweb.common.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @Enumerated(EnumType.STRING)
    EType type;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(name = "sending_time")
    LocalDateTime sendingTime;

    @ManyToOne
    @JoinColumn(name = "sender_username")
    User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_username")
    User recipient;

    public enum EType {
        TEXT,
        IMAGE,
        COURSE_INFO
    }

    @PrePersist
    @PreUpdate
    public void initializeSendingTime() {
        if (this.sendingTime == null) {
            this.sendingTime = LocalDateTime.now();
        }
    }
}
