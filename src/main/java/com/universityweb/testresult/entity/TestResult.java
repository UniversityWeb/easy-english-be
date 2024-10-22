package com.universityweb.testresult.entity;

import com.universityweb.common.auth.entity.User;
import com.universityweb.test.entity.Test;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String result;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @Column(name = "taking_duration")
    private Integer takingDuration;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test test;

    public enum EStatus {
        DONE, IN_PROGRESS
    }
}
