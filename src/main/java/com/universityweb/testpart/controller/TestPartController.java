package com.universityweb.testpart.controller;

import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.request.AddTestPartRequest;
import com.universityweb.testpart.service.TestPartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-parts")
@RequiredArgsConstructor
@Tag(name = "Test Parts")
public class TestPartController {

    private static final Logger log = LogManager.getLogger(TestPartController.class);

    private final TestPartService testPartService;

    @PostMapping("/add")
    public ResponseEntity<TestPartDTO> createTestPart(
            @RequestBody AddTestPartRequest addTestPartRequest
    ) {
        log.info("Received createTestPart request: {}", addTestPartRequest);
        TestPartDTO createdTestPart = testPartService.createTestPart(addTestPartRequest);
        log.info("Created TestPart: {}", createdTestPart);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestPart);
    }

    @PutMapping("/update")
    public ResponseEntity<TestPartDTO> updateTestPart(
            @RequestBody TestPartDTO testPartDTO
    ) {
        log.info("Received updateTestPart request for id: {}", testPartDTO.id());
        TestPartDTO updatedTestPart = testPartService.updateTestPart(testPartDTO);
        log.info("Updated TestPart: {}", updatedTestPart);
        return ResponseEntity.ok(updatedTestPart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestPartDTO> getTestPartById(
            @PathVariable Long id
    ) {
        log.info("Fetching TestPart by id: {}", id);
        TestPartDTO testPart = testPartService.getTestPartById(id);
        log.info("Found TestPart: {}", testPart);
        return ResponseEntity.ok(testPart);
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<TestPartDTO>> getTestPartsByTestId(
            @PathVariable Long testId
    ) {
        log.info("Fetching TestParts by testId: {}", testId);
        List<TestPartDTO> testParts = testPartService.getTestPartsByTestId(testId);
        log.info("TestParts found for testId {}: {}", testId, testParts);
        return ResponseEntity.ok(testParts);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTestPart(@PathVariable Long id) {
        testPartService.deleteTestPart(id);
        return ResponseEntity.noContent().build();
    }
}
