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

    @ManyToOne
    @JoinColumn(name = "course_section_id", referencedColumnName = "id")
    private Section section;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestPart> parts;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections;
}
