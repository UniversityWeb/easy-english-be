package com.universityweb.testpart.controller;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.service.TestPartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-parts")
@Tag(name = "Test Parts")
public class TestPartController
        extends BaseController<TestPart, TestPartDTO, Long, TestPartService> {

    @Autowired
    public TestPartController(TestPartService service) {
        super(service);
    }

    @GetMapping("/get-by-test/{testId}")
    public ResponseEntity<List<TestPartDTO>> getTestPartsByTestId(
            @PathVariable Long testId
    ) {
        log.info("Fetching TestParts by testId: {}", testId);
        List<TestPartDTO> testParts = service.getTestPartsByTestId(testId);
        log.info("TestParts found for testId {}: {}", testId, testParts);
        return ResponseEntity.ok(testParts);
    }
}
