package com.universityweb.testresult.controller;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.testresult.request.AddTestResultRequest;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.service.TestResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test-results")
@RequiredArgsConstructor
@Tag(name = "Test Results")
public class TestResultController {

    private static final Logger log = LogManager.getLogger(TestResultController.class);

    private final TestResultService testResultService;
    private final AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<TestResultDTO> createTestResult(
            @RequestBody AddTestResultRequest testResultRequest
    ) {
        log.info("Received createTestResult request: {}", testResultRequest);
        TestResultDTO createdResult = testResultService.createTestResult(testResultRequest);
        log.info("Created TestResult: {}", createdResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResult);
    }

    @PutMapping("/update")
    public ResponseEntity<TestResultDTO> updateTestResult(
            @RequestBody TestResultDTO testResultDTO
    ) {
        log.info("Received updateTestResult request for id: {}", testResultDTO.id());
        TestResultDTO updatedResult = testResultService.updateTestResult(testResultDTO);
        log.info("Updated TestResult: {}", updatedResult);
        return ResponseEntity.ok(updatedResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResultDTO> getTestResultById(
            @PathVariable Long id
    ) {
        log.info("Fetching TestResult by id: {}", id);
        TestResultDTO testResult = testResultService.getTestResultById(id);
        log.info("Found TestResult: {}", testResult);
        return ResponseEntity.ok(testResult);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<TestResultDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = authService.getCurrentUsername();
        log.info("Fetching TestResults for user: {}", username);
        Page<TestResultDTO> testResults = testResultService.getAll(page, size);
        log.info("TestResults found for user {}: size-{}", username, testResults.getSize());
        return ResponseEntity.ok(testResults);
    }

    @GetMapping("/get-by-cur-user")
    public ResponseEntity<Page<TestResultDTO>> getTestResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = authService.getCurrentUsername();
        log.info("Fetching all for user: {}", username);
        Page<TestResultDTO> testResults = testResultService.getTestResultsByUsername(page, size, username);
        log.info("all test results found for user {}: size-{}", username, testResults.getSize());
        return ResponseEntity.ok(testResults);
    }
}
