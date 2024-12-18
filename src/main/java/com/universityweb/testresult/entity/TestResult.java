package com.universityweb.testresult.entity;

import com.universityweb.common.auth.entity.User;
import com.universityweb.test.entity.Test;
import com.universityweb.useranswer.entity.UserAnswer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

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
@Where(clause = "is_deleted = false")
public class TestResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String result;

    @Column(name = "correct_percent")
    private Double correctPercent;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @Column(name = "taking_duration")
    private Integer takingDuration;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test test;

    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordinalNumber ASC")
    private List<UserAnswer> userAnswers;

    public enum EStatus {
        DONE, IN_PROGRESS, FAILED
    }

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
