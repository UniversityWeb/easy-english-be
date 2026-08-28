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

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "created_by")
    String createdBy;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "updated_by")
    String updatedBy;

    @Column(name = "is_recalled", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isRecalled = false;

    @Column(name = "deleted_by_sender", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean deletedBySender = false;

    @Column(name = "deleted_by_recipient", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean deletedByRecipient = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    EMessageStatus status = EMessageStatus.SENT;

    @Column(name = "read_at")
    LocalDateTime readAt;

    @Column(name = "reply_to_id")
    UUID replyToId;

    @Column(name = "reply_to_content")
    String replyToContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "reply_to_type")
    EType replyToType;

    @Column(name = "reply_to_sender")
    String replyToSender;

    public enum EMessageStatus {
        SENT,
        DELIVERED,
        READ
    }

    public enum EType {
        TEXT,
        IMAGE,
        COURSE_INFO,
        FILE,
        LOCATION,
        CONTACT,
        READ_RECEIPT,
        DELIVERED_RECEIPT
    }

    @PrePersist
    public void prePersist() {
        if (this.sendingTime == null) {
            this.sendingTime = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
