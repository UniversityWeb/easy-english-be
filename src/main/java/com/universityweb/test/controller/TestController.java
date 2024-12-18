package com.universityweb.test.controller;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testresult.service.TestResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/tests")
@Tag(name = "Tests")
public class TestController
        extends BaseController<Test, TestDTO, Long, TestService> {

    private final MediaService mediaService;
    private final AuthService authService;
    private final TestResultService testResultService;

    @Autowired
    public TestController(
            TestService service,
            MediaService mediaService,
            AuthService authService,
            TestResultService testResultService
    ) {
        super(service);
        this.mediaService = mediaService;
        this.authService = authService;
        this.testResultService = testResultService;
    }

    @Override
    public ResponseEntity<TestDTO> getById(Long id) {
        service.refactorOrdinalNumbers(id);
        TestDTO testDTO = service.getById(id);
        return ResponseEntity.ok(MediaUtils.attachTestMediaUrls(mediaService, testDTO));
    }

    @PutMapping("/{testId}/upload-audio")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<String> updateAudioFile(
            @PathVariable Long testId,
            @RequestParam("audio") MultipartFile audio
    ) {
        Test test = service.getEntityById(testId);
        mediaService.deleteFile(test.getAudioPath());

        String suffixPath = mediaService.uploadFile(audio);

        test.setAudioPath(suffixPath);
        Test saved = service.save(test);
        return ResponseEntity.ok(mediaService.constructFileUrl(saved.getAudioPath()));
    }

    @DeleteMapping("/{testId}/delete-audio")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteAudioFile(
            @PathVariable Long testId
    ) {
        Test test = service.getEntityById(testId);
        mediaService.deleteFile(test.getAudioPath());

        test.setAudioPath("");
        service.save(test);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-status/{id}/{status}")
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
        String username = authService.getCurrentUsername();
        log.info("get tests by section Id: {}", sectionId);
        List<TestDTO> testDTOs = service.getBySection(username, sectionId);
        return ResponseEntity.ok(addTestMediaUrls(testDTOs));
    }

    @PutMapping("/refactor-ordinal-numbers/{testId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> refactorOrdinalNumbers(
            @PathVariable Long testId
    ) {
        service.refactorOrdinalNumbers(testId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/is-empty/{testId}")
    public ResponseEntity<Boolean> isEmptyTest(
            @PathVariable Long testId
    ) {
        Boolean isEmpty = service.isEmptyTest(testId);
        return ResponseEntity.ok(isEmpty);
    }

    private List<TestDTO> addTestMediaUrls(List<TestDTO> testDTOs) {
        return testDTOs.stream()
                .map(this::addTestMediaUrls)
                .collect(Collectors.toList());
    }

    private TestDTO addTestMediaUrls(TestDTO testDTO) {
        String username = authService.getCurrentUsername();
        boolean isDone = testResultService.isDone(username, testDTO.getId());
        testDTO.setIsDone(isDone);
        return MediaUtils.attachTestMediaUrls(mediaService, testDTO);
    }
}
