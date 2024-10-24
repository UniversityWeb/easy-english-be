package com.universityweb.useranswer.service;

import com.universityweb.common.auth.service.auth.AuthService;
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

    private AuthService authService;

    @Autowired
    public UserAnswerServiceImpl(UserAnswerRepos repository, AuthService authService) {
        super(repository, UserAnswerMapper.INSTANCE);
        this.authService = authService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find user answer with id " + id);
    }

    @Override
    public UserAnswerDTO update(UserAnswerDTO dto) {
        return null;
    }

    @Override
    public void softDelete(Long aLong) {

    }
}
