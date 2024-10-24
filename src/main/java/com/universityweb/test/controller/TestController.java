package com.universityweb.test.controller;

import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
@Tag(name = "Tests")
public class TestController {

    private static final Logger log = LogManager.getLogger(TestController.class);

    private final TestService testService;

    @GetMapping("/get-all")
    public ResponseEntity<List<TestDTO>> getAllTests() {
        log.info("Fetching all tests");
        List<TestDTO> tests = testService.getAllTests();
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDTO> getTestById(
            @PathVariable Long id
    ) {
        log.info("Fetching test with ID: {}", id);
        TestDTO testDTO = testService.getTestById(id);
        return ResponseEntity.ok(testDTO);
    }

    @PostMapping("/add")
    public ResponseEntity<TestDTO> createTest(
            @RequestBody TestDTO testDTO
    ) {
        log.info("Creating new test: {}", testDTO);
        TestDTO createdTest = testService.createTest(testDTO);
        return ResponseEntity.status(201).body(createdTest);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TestDTO> updateTest(
            @RequestBody TestDTO testDTO
    ) {
        log.info("Updating test with ID: {}", testDTO.getId());
        TestDTO updatedTest = testService.updateTest(testDTO);
        return ResponseEntity.ok(updatedTest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTest(
            @PathVariable Long id
    ) {
        log.info("Deleting test with ID: {}", id);
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @PathVariable Test.EStatus status
    ) {
        log.info("Update test status with ID: {}", id);
        testService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
