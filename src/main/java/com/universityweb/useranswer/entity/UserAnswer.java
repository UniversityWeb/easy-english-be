package com.universityweb.useranswer.entity;

import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testresult.entity.TestResult;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user_answers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserAnswer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "user_answer_strs",
            joinColumns = @JoinColumn(name = "user_answer_id")
    )
    @Column(name = "answer", columnDefinition = "TEXT")
    private List<String> answers;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "test_question_id", referencedColumnName = "id")
    private TestQuestion testQuestion;

    @ManyToOne
    @JoinColumn(name = "test_result_id", referencedColumnName = "id")
    private TestResult testResult;
}
