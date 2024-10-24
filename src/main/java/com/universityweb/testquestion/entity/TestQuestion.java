package com.universityweb.testquestion.entity;

import com.universityweb.questiongroup.QuestionGroup;
import com.universityweb.useranswer.entity.UserAnswer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "test_questions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EType type;

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    private String title;

    private String description;

    @Column(name = "audio_path", nullable = true)
    private String audioPath;

    @Column(name = "image_path", nullable = true)
    private String imagePath;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option")
    private List<String> options;

    @ElementCollection
    @CollectionTable(name = "question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "correct_answer")
    private List<String> correctAnswers;

    @ManyToOne
    @JoinColumn(name = "question_group_id", referencedColumnName = "id")
    private QuestionGroup questionGroup;

    @OneToMany(mappedBy = "testQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAnswer> userAnswers;

    public enum EType {
        SINGLE_CHOICE,
        MULTIPLE_CHOICE,
        MATCHING,
        FILL_BLANK,
    }
}
