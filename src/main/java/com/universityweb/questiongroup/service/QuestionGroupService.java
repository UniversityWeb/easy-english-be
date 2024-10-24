package com.universityweb.questiongroup.service;

import com.universityweb.questiongroup.QuestionGroup;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;

import java.util.List;

public interface QuestionGroupService {
    List<QuestionGroupDTO> getAllQuestionGroups();
    QuestionGroupDTO getQuestionGroupById(Long id);
    QuestionGroupDTO createQuestionGroup(QuestionGroupDTO questionGroupDTO);
    QuestionGroupDTO updateQuestionGroup(QuestionGroupDTO questionGroupDTO);
    void deleteQuestionGroup(Long id);
    QuestionGroup getEntityById(Long id);
}
