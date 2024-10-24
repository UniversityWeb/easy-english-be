package com.universityweb.questiongroup.dto;

import com.universityweb.testquestion.dto.TestQuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionGroupDTO {
    private Long id;
    private Integer ordinalNumber;
    private Integer from;
    private Integer to;
    private String title;
    private String requirement;
    private String audioPath;
    private String imagePath;
    private String contentToDisplay;
    private String originalContent;
    private Boolean isDeleted;
    private List<TestQuestionDTO> questions;
    private Long testPartId;
}
