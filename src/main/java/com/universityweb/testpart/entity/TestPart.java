package com.universityweb.testpart.entity;

import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.test.entity.Test;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.Comparator;
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

    @Column(name = "ordinal_number", columnDefinition = "integer default 1")
    private Integer ordinalNumber;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @OneToMany(mappedBy = "testPart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordinalNumber ASC")
    private List<QuestionGroup> questionGroups;

    @PostLoad
    private void postLoad() {
        if (this.questionGroups != null) {
            this.questionGroups.sort((g1, g2) -> {
                Integer ordinal1 = g1.getOrdinalNumber();
                Integer ordinal2 = g2.getOrdinalNumber();

                int value1 = ordinal1 != null ? ordinal1 : Integer.MAX_VALUE;
                int value2 = ordinal2 != null ? ordinal2 : Integer.MAX_VALUE;

                return Integer.compare(value1, value2);
            });
        }
    }
}
