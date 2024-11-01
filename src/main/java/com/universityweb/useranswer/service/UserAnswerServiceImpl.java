package com.universityweb.useranswer.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.testquestion.service.TestQuestionService;
import com.universityweb.testresult.service.TestResultService;
import com.universityweb.useranswer.UserAnswerMapper;
import com.universityweb.useranswer.UserAnswerRepos;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import com.universityweb.useranswer.entity.UserAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAnswerServiceImpl extends BaseServiceImpl<UserAnswer, UserAnswerDTO, Long, UserAnswerRepos, UserAnswerMapper>
        implements UserAnswerService {

    private final TestQuestionService testQuestionService;
    private final TestResultService testResultService;

    @Autowired
    public UserAnswerServiceImpl(UserAnswerRepos repository, TestQuestionService testQuestionService, TestResultService testResultService) {
        super(repository, UserAnswerMapper.INSTANCE);
        this.testQuestionService = testQuestionService;
        this.testResultService = testResultService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find user answer with id " + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(UserAnswer entity, UserAnswerDTO dto) {
        entity.setTestQuestion( testQuestionService.getEntityById(dto.getTestQuestionId()) );
        entity.setTestResult( testResultService.getEntityById(dto.getTestResultId()) );
    }

    @Override
    public UserAnswerDTO update(Long id, UserAnswerDTO dto) {
        UserAnswer userAnswer = getEntityById(id);

        userAnswer.setAnswers(dto.getAnswers());
        userAnswer.setIsCorrect(dto.getIsCorrect());

        UserAnswer saved = repository.save(userAnswer);
        return mapper.toDTO(saved);
    }
}
