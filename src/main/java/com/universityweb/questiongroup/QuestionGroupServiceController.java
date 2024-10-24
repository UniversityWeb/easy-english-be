package com.universityweb.questiongroup;

import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.service.QuestionGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question-group")
@RequiredArgsConstructor
@Tag(name = "Question Groups")
public class QuestionGroupServiceController {

    private static final Logger log = LogManager.getLogger(QuestionGroupServiceController.class);

    private final QuestionGroupService questionGroupService;

    @GetMapping("/get-all")
    public ResponseEntity<List<QuestionGroupDTO>> getAllQuestionGroups() {
        log.info("Fetching all question groups");
        List<QuestionGroupDTO> questionGroups = questionGroupService.getAllQuestionGroups();
        return ResponseEntity.ok(questionGroups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionGroupDTO> getQuestionGroupById(@PathVariable Long id) {
        log.info("Fetching question group with ID: {}", id);
        QuestionGroupDTO questionGroupDTO = questionGroupService.getQuestionGroupById(id);
        return ResponseEntity.ok(questionGroupDTO);
    }

    @PostMapping("/add")
    public ResponseEntity<QuestionGroupDTO> createQuestionGroup(@RequestBody QuestionGroupDTO questionGroupDTO) {
        log.info("Creating new question group: {}", questionGroupDTO);
        QuestionGroupDTO createdQuestionGroup = questionGroupService.createQuestionGroup(questionGroupDTO);
        return ResponseEntity.status(201).body(createdQuestionGroup);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<QuestionGroupDTO> updateQuestionGroup(
            @RequestBody QuestionGroupDTO questionGroupDTO
    ) {
        log.info("Updating question group with ID: {}", questionGroupDTO);
        QuestionGroupDTO updatedQuestionGroup = questionGroupService.updateQuestionGroup(questionGroupDTO);
        return ResponseEntity.ok(updatedQuestionGroup);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteQuestionGroup(@PathVariable Long id) {
        log.info("Deleting question group with ID: {}", id);
        questionGroupService.deleteQuestionGroup(id);
        return ResponseEntity.noContent().build();
    }
}
