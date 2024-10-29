package com.universityweb.testquestion;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testquestion.service.TestQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-questions")
@Tag(name = "Test Questions")
public class TestQuestionController
        extends BaseController<TestQuestion, TestQuestionDTO, Long, TestQuestionService> {

    @Autowired
    public TestQuestionController(TestQuestionService service) {
        super(service);
    }

    @GetMapping("/get-by-question-group/{questionGroupId}")
    public ResponseEntity<List<TestQuestionDTO>> getByQuestionGroup(
            @PathVariable Long questionGroupId
    ) {
        log.info("Fetching test questions for question group ID: {}", questionGroupId);

        List<TestQuestionDTO> questions = service.getByQuestionGroupId(questionGroupId);
        log.info("Retrieved {} questions for question group ID: {}", questions.size(), questionGroupId);

        return ResponseEntity.ok(questions);
    }
}
