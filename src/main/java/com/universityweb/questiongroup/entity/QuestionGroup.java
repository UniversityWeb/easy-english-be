package com.universityweb.questiongroup.entity;

import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testquestion.entity.TestQuestion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.Comparator;
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

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @Column(name = "from_question")
    private Integer from;

    @Column(name = "to_question")
    private Integer to;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String requirement;

    @Column(name = "image_path")
    private String imagePath;

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
            this.questions.sort(Comparator.comparingInt(TestQuestion::getOrdinalNumber));
        }
    }
}
