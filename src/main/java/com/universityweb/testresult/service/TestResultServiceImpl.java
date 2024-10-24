package com.universityweb.testresult.service;

import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.mapper.TestResultMapper;
import com.universityweb.testresult.request.AddTestResultRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TestResultServiceImpl implements TestResultService {
    private final TestResultMapper testResultMapper = TestResultMapper.INSTANCE;

    @Autowired
    private TestResultRepos testResultRepos;

    @Override
    public TestResultDTO createTestResult(AddTestResultRequest addTestResultRequest) {
        TestResult testResult = testResultMapper.toEntity(addTestResultRequest);
        TestResult savedResult = testResultRepos.save(testResult);
        return testResultMapper.toDTO(savedResult);
    }

    @Override
    public TestResultDTO updateTestResult(TestResultDTO testResultDTO) {
        TestResult testResult = getEntityById(testResultDTO.id());

        testResult.setResult(testResultDTO.result());
        testResult.setStatus(testResultDTO.status());
        testResult.setTakingDuration(testResultDTO.takingDuration());
        testResult.setStartedAt(testResultDTO.startedAt());
        testResult.setFinishedAt(testResultDTO.finishedAt());

        TestResult updatedResult = testResultRepos.save(testResult);
        return testResultMapper.toDTO(updatedResult);
    }

    @Override
    public TestResultDTO getTestResultById(Long id) {
        TestResult testResult = getEntityById(id);
        return testResultMapper.toDTO(testResult);
    }

    @Override
    public Page<TestResultDTO> getTestResultsByUsername(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<TestResult> testResultsPage = new PageImpl<>(new ArrayList<>());
//        testResultRepos.findByUser_Username(username, pageable);
        return testResultsPage.map(testResultMapper::toDTO);
    }

    @Override
    public TestResult getEntityById(Long id) {
        return testResultRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find any test results with id" + id));
    }

    @Override
    public Page<TestResultDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestResult> testResultsPage = testResultRepos.findAll(pageable);
        return testResultsPage.map(testResultMapper::toDTO);
    }
}
