package com.universityweb.testresult.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.dto.TestResultWithoutListDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.request.GetTestResultReq;
import com.universityweb.testresult.request.SubmitTestRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TestResultService extends BaseService<TestResult, TestResultDTO, Long> {
    Page<TestResultWithoutListDTO> getTestHistoryByTestId(GetTestResultReq getTestResultReq);
    List<TestResult> getByUsernameAndTestId(String username, Long targetId);
    Boolean isDone(String username, Long targetId);
    TestResultDTO submit(String username, SubmitTestRequest submitTestRequest);
    Page<TestResultDTO> getByCurUser(String username, int page, int size);
}
