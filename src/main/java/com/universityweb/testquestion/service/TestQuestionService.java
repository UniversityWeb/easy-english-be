package com.universityweb.testquestion.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.testquestion.AddQuizQuestionRequest;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;

import java.util.List;

public interface TestQuestionService extends BaseService<TestQuestion, TestQuestionDTO, Long> {
    List<TestQuestionDTO> getByQuestionGroupId(Long questionGroupId);
    TestQuestionDTO createNewQuestionForQuizType(AddQuizQuestionRequest request);
    List<TestQuestionDTO> getAllQuestionsForQuizType(Long testId);
}
