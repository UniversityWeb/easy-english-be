package com.universityweb.test.entity;

import com.universityweb.section.entity.Section;
import com.universityweb.testpart.entity.TestPart;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Test implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    private String title;

    private String description;

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @Column(name = "duration_in_milis")
    private Integer durationInMilis;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestPart> parts;

    @ManyToOne
    @JoinColumn(name = "course_section_id", referencedColumnName = "id", nullable = false)
    private Section section;

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
