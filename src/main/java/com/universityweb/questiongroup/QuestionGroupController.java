package com.universityweb.questiongroup;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.service.QuestionGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question-groups")
@Tag(name = "Question Groups")
public class QuestionGroupController
        extends BaseController<QuestionGroup, QuestionGroupDTO, Long, QuestionGroupService> {

    @Autowired
    public QuestionGroupController(QuestionGroupService service) {
        super(service);
    }

    @GetMapping("/get-by-test-part/{testPartId}")
    public ResponseEntity<List<QuestionGroupDTO>> getByTestPart(
            @PathVariable Long testPartId
    ) {
        log.info("Fetching test questions for test part ID: {}", testPartId);
        List<QuestionGroupDTO> questionGroupDTOs = service.getByTestPartId(testPartId);
        return ResponseEntity.ok(questionGroupDTOs);
    }
}
