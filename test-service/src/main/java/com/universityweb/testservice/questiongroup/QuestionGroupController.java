package com.universityweb.testservice.questiongroup;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.service.QuestionGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question-groups")
@Tag(name = "Question Groups")
public class QuestionGroupController
        extends BaseController<QuestionGroup, QuestionGroupDTO, Long, QuestionGroupService> {

    private final MediaService mediaService;

    @Autowired
    public QuestionGroupController(
            QuestionGroupService service,
            MediaService mediaService
    ) {
        super(service);
        this.mediaService = mediaService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Override
    public ResponseEntity<QuestionGroupDTO> create(QuestionGroupDTO dto) {
        return super.create(dto);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Override
    public ResponseEntity<QuestionGroupDTO> update(Long id, QuestionGroupDTO dto) {
        return super.update(id, dto);
    }

    @GetMapping("/get-by-test-part/{testPartId}")
    public ResponseEntity<List<QuestionGroupDTO>> getByTestPart(
            @PathVariable Long testPartId
    ) {
        log.info("Fetching test questions for test part ID: {}", testPartId);
        List<QuestionGroupDTO> questionGroupDTOs = service.getByTestPartId(testPartId);
        return ResponseEntity.ok(questionGroupDTOs);
    }

    @PutMapping("/swap/{questionGroupId1}/{questionGroupId2}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> swapTestPart(
            @PathVariable Long questionGroupId1,
            @PathVariable Long questionGroupId2
    ) {
        service.swapTwoQuestionGroups(questionGroupId1, questionGroupId2);
        return ResponseEntity.noContent().build();
    }
}
