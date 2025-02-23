package com.universityweb.testservice.testquestion;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testquestion.service.TestQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create-new-question-for-quiz")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<TestQuestionDTO> createNewQuestionForQuizType(
            @RequestBody AddQuizQuestionRequest request
    ) {
        TestQuestionDTO saved = service.createNewQuestionForQuizType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get-questions-for-quiz/{testId}")
    public ResponseEntity<List<TestQuestionDTO>> getAllQuestionsForQuizType(
            @PathVariable Long testId
    ) {
        List<TestQuestionDTO> testQuestionDTOs = service.getAllQuestionsForQuizType(testId);
        return ResponseEntity.ok(testQuestionDTOs);
    }

    @PutMapping("/swap/{questionId1}/{questionId2}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> swapTestQuestions(
            @PathVariable Long questionId1,
            @PathVariable Long questionId2
    ) {
        service.swapTwoQuestions(questionId1, questionId2);
        return ResponseEntity.noContent().build();
    }
}
