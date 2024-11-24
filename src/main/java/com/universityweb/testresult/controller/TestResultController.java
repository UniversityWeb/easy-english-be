package com.universityweb.testresult.controller;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.dto.TestResultWithoutListDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.request.GetTestResultReq;
import com.universityweb.testresult.request.SubmitTestRequest;
import com.universityweb.testresult.service.TestResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test-results")
@Tag(name = "Test Results")
public class TestResultController
        extends BaseController<TestResult, TestResultDTO, Long, TestResultService> {

    private final AuthService authService;

    @Autowired
    public TestResultController(
            TestResultService service,
            AuthService authService
    ) {
        super(service);
        this.authService = authService;
    }

    @GetMapping("/get-all-by-page")
    public ResponseEntity<Page<TestResultDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = authService.getCurrentUsername();
        log.info("Fetching TestResults for user: {}", username);
        Page<TestResultDTO> testResults = service.getAll(page, size);
        log.info("TestResults found for user {}: size-{}", username, testResults.getSize());
        return ResponseEntity.ok(testResults);
    }

    @GetMapping("/get-test-history-by-test-id")
    public ResponseEntity<Page<TestResultWithoutListDTO>> getTestHistoryByTestId(
            @RequestParam Long testId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching test history for user");
        GetTestResultReq getTestResultReq = new GetTestResultReq(testId, page, size);
        Page<TestResultWithoutListDTO> testResults = service.getTestHistoryByTestId(getTestResultReq);
        log.info("all test results found: size-{}", testResults.getSize());
        return ResponseEntity.ok(testResults);
    }

    @PostMapping("/submit-test")
    public ResponseEntity<TestResultDTO> submit(
            @RequestBody SubmitTestRequest submitTestRequest
    ) {
        String username = authService.getCurrentUsername();
        TestResultDTO testResultDTO = service.submit(username, submitTestRequest);
        return ResponseEntity.ok(testResultDTO);
    }
}
