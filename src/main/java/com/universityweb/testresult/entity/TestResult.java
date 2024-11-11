package com.universityweb.testresult.entity;

import com.universityweb.common.auth.entity.User;
import com.universityweb.test.entity.Test;
import com.universityweb.useranswer.entity.UserAnswer;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAnswer> userAnswers;

    public enum EStatus {
        DONE, IN_PROGRESS, FAILED
    }
}
