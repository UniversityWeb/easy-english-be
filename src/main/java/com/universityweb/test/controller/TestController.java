package com.universityweb.test.controller;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/tests")
@Tag(name = "Tests")
public class TestController
        extends BaseController<Test, TestDTO, Long, TestService> {


    @Autowired
    public TestController(TestService service) {
        super(service);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @PathVariable Test.EStatus status
    ) {
        log.info("Update test status with ID: {}", id);
        service.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-by-section/{sectionId}")
    public ResponseEntity<List<TestDTO>> getBySection(
            @PathVariable Long sectionId
    ) {
        log.info("get tests by section Id: {}", sectionId);
        List<TestDTO> testDTOs = service.getBySection(sectionId);
        return ResponseEntity.ok(testDTOs);
    }
}
