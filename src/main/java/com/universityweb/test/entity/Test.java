package com.universityweb.test.entity;

import com.universityweb.section.entity.Section;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testquestion.entity.TestQuestion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "tests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "status != 'DELETED'")
public class Test implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EType type;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "ordinal_number", columnDefinition = "integer default 1")
    private Integer ordinalNumber;

    @Column(name = "duration_in_milis")
    private Integer durationInMilis;

    @Column(name = "passing_grade")
    private Double passingGrade;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "audio_path")
    private String audioPath;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordinalNumber ASC")
    private List<TestPart> parts;

    @ManyToOne
    @JoinColumn(name = "course_section_id", referencedColumnName = "id", nullable = false)
    private Section section;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // Ensures createdAt is set if not already
        }
        if (type == null) {
            type = EType.QUIZ; // Ensures default type is set
        }
        if (status == null) {
            status = EStatus.DRAFT; // Ensures default status is set
        }
        if (ordinalNumber == null) {
            ordinalNumber = 1; // Ensures default ordinalNumber is set
        }
        if (durationInMilis == null) {
            durationInMilis = 3600000; // Ensures default duration is set
        }
        if (passingGrade == null) {
            passingGrade = 0.0; // Ensures default passing grade is set
        }
        if (audioPath == null) {
            audioPath = ""; // Ensures default audioPath is set
        }
    }

    @PostLoad
    private void postLoad() {
        if (this.parts != null) {
            this.parts.sort((p1, p2) -> {
                Integer ordinal1 = p1.getOrdinalNumber();
                Integer ordinal2 = p2.getOrdinalNumber();

                int value1 = ordinal1 != null ? ordinal1 : Integer.MAX_VALUE;
                int value2 = ordinal2 != null ? ordinal2 : Integer.MAX_VALUE;

                return Integer.compare(value1, value2);
            });
        }

    }

    public enum EType {
        QUIZ,
        CUSTOM
    }

    public enum EStatus {
        /**
         * The test is currently visible and available to students.
         */
        DISPLAY,

        /**
         * The test is hidden from students, possibly during preparation.
         */
        HIDE,

        /**
         * The test is still in the creation phase and not ready for student access.
         */
        DRAFT,

        /**
         * The test has been deleted from the system and is no longer accessible.
         */
        DELETED
    }
}
