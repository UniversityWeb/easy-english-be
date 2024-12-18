package com.universityweb.questiongroup.entity;

import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testquestion.entity.TestQuestion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "test_question_groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Where(clause = "is_deleted = false")
public class QuestionGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordinal_number", columnDefinition = "integer default 1")
    private Integer ordinalNumber;

    @Column(name = "from_question", columnDefinition = "integer default 1")
    private Integer from;

    @Column(name = "to_question", columnDefinition = "integer default 1")
    private Integer to;

    @Column(columnDefinition = "TEXT")
    private String requirement;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "questionGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ordinalNumber ASC")
    private List<TestQuestion> questions;

    @ManyToOne
    @JoinColumn(name = "test_part_id", referencedColumnName = "id")
    private TestPart testPart;

    @PostLoad
    private void postLoad() {
        if (this.questions != null) {
            this.questions.sort((q1, q2) -> {
                Integer ordinal1 = q1.getOrdinalNumber();
                Integer ordinal2 = q2.getOrdinalNumber();

                int value1 = ordinal1 != null ? ordinal1 : Integer.MAX_VALUE;
                int value2 = ordinal2 != null ? ordinal2 : Integer.MAX_VALUE;

                return Integer.compare(value1, value2);
            });
        }
    }

    @PrePersist
    @PreUpdate
    private void setDefaults() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
