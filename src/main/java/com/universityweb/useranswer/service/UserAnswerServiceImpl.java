package com.universityweb.useranswer.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.useranswer.UserAnswerMapper;
import com.universityweb.useranswer.UserAnswerRepos;
import com.universityweb.useranswer.entity.UserAnswer;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAnswerServiceImpl extends BaseServiceImpl<UserAnswer, UserAnswerDTO, Long, UserAnswerRepos, UserAnswerMapper>
        implements UserAnswerService {

    @Autowired
    public UserAnswerServiceImpl(UserAnswerRepos repository) {
        super(repository, UserAnswerMapper.INSTANCE);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find user answer with id " + id);
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
