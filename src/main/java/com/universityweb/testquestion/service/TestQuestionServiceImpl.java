package com.universityweb.testquestion.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.service.QuestionGroupService;
import com.universityweb.testquestion.TestQuestionMapper;
import com.universityweb.testquestion.TestQuestionRepos;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestQuestionServiceImpl
        extends BaseServiceImpl<TestQuestion, TestQuestionDTO, Long, TestQuestionRepos, TestQuestionMapper>
        implements TestQuestionService {

    private final QuestionGroupService questionGroupService;

    @Autowired
    public TestQuestionServiceImpl(TestQuestionRepos repository, QuestionGroupService questionGroupService) {
        super(repository, TestQuestionMapper.INSTANCE);
        this.questionGroupService = questionGroupService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("TestQuestion Not Found with id=" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(TestQuestion entity, TestQuestionDTO dto) {
        entity.setQuestionGroup(questionGroupService.getEntityById(dto.questionGroupId()));
    }

    @Override
    public TestQuestionDTO update(Long id, TestQuestionDTO dto) {
        TestQuestion testQuestion = getEntityById(id);
        testQuestion = mapper.toEntity(dto);
        TestQuestion saved = repository.save(testQuestion);
        return mapper.toDTO(saved);
    }
}
