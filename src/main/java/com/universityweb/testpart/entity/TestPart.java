package com.universityweb.testpart.entity;

import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.test.entity.Test;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "test_parts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "is_deleted = false")
public class TestPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "reading_passage", columnDefinition = "TEXT", nullable = true)
    private String readingPassage;

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @OneToMany(mappedBy = "testPart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionGroup> questionGroups;
}
