package com.universityweb.common.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Token implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String tokenStr;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(columnDefinition = "TEXT")
    private String refreshTokenStr;

    @Column(name = "refresh_expiry_date")
    private LocalDateTime refreshExpiryDate;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_location")
    private String loginLocation;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.tokenStr == null) {
            this.tokenStr = "";
        }

        if (this.expiryDate == null) {
            this.expiryDate = LocalDateTime.now().plusHours(1);
        }
    }
}
