package com.universityweb.testresult.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.mapper.TestResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TestResultServiceImpl
        extends BaseServiceImpl<TestResult, TestResultDTO, Long, TestResultRepos, TestResultMapper>
        implements TestResultService {

    @Autowired
    public TestResultServiceImpl(
            TestResultRepos repository,
            TestResultMapper mapper
    ) {
        super(repository, mapper);
    }

    @Override
    public Page<TestResultDTO> getTestResultsByUsername(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<TestResult> testResultsPage = repository.findByUser_Username(username, pageable);
        return testResultsPage.map(mapper::toDTO);
    }

    @Override
    public TestResult getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find any test results with id" + id));
    }

    @Override
    public TestResultDTO update(Long aLong, TestResultDTO dto) {
        TestResult testResult = getEntityById(dto.id());

        testResult.setResult(dto.result());
        testResult.setStatus(dto.status());
        testResult.setTakingDuration(dto.takingDuration());
        testResult.setStartedAt(dto.startedAt());
        testResult.setFinishedAt(dto.finishedAt());

        return savedAndConvertToDTO(testResult);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any test results with id=" + id);
    }

    @Override
    public Page<TestResultDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestResult> testResultsPage = repository.findAll(pageable);
        return testResultsPage.map(mapper::toDTO);
    }

    @Override
    public TestResult getByUsernameAndTestId(String username, Long testId) {
        return repository.findByUser_UsernameAndTest_Id(username, testId)
                .orElse(null);
    }

    @Override
    public Boolean isDone(String username, Long testId) {
        TestResult testResult = getByUsernameAndTestId(username, testId);
        if (testResult == null) return false;
        return testResult.getStatus().equals(TestResult.EStatus.DONE);
    }
}
