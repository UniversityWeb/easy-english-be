package com.universityweb.testresult.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import org.springframework.data.domain.Page;

public interface TestResultService extends BaseService<TestResult, TestResultDTO, Long> {
    Page<TestResultDTO> getTestResultsByUsername(int page, int size, String username);
    Page<TestResultDTO> getAll(int page, int size);
    TestResult getByUsernameAndTestId(String username, Long targetId);
    Boolean isDone(String username, Long targetId);
}
