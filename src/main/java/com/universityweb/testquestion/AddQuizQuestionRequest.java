package com.universityweb.testquestion;

import com.universityweb.testquestion.entity.TestQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddQuizQuestionRequest {
    private Long id;
    private TestQuestion.EType type;
    private Integer ordinalNumber;
    private String title;
    private String description;
    private String audioPath;
    private String imagePath;
    private List<String> options;
    private List<String> correctAnswers;

    private Long testId;
}
