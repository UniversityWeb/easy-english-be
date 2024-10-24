package com.universityweb.testquestion.service;

import com.universityweb.testquestion.TestQuestionMapper;
import org.springframework.stereotype.Service;

@Service
public class TestQuestionServiceImpl implements TestQuestionService {
    private final TestQuestionMapper testQuestionMapper = TestQuestionMapper.INSTANCE;
}
