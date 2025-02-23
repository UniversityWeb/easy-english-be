package com.universityweb.testservice.questiongroup.dto;

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
    private String requirement;
    private List<TestQuestionDTO> questions;
    private Long testPartId;
}
