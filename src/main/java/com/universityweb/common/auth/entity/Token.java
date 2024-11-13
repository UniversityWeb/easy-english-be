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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.tokenStr == null) {
            this.tokenStr = "";
        }

        if (this.expiryDate == null) {
            this.expiryDate = LocalDateTime.now().plusHours(1);
        }
    }
}
