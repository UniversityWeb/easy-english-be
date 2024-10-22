package com.universityweb.testresult.service;

import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.request.AddTestResultRequest;
import org.springframework.data.domain.Page;

public interface TestResultService {
    TestResultDTO createTestResult(AddTestResultRequest addTestResultRequest);
    TestResultDTO updateTestResult(TestResultDTO testResultDTO);
    TestResultDTO getTestResultById(Long id);
    Page<TestResultDTO> getTestResultsByUsername(int page, int size, String username);
    TestResult getEntityById(Long id);
    Page<TestResultDTO> getAll(int page, int size);
}
