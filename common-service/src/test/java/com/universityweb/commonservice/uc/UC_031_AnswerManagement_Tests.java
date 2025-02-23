package com.universityweb.commonservice.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.service.QuestionGroupService;
import com.universityweb.testquestion.TestQuestionMapper;
import com.universityweb.testquestion.TestQuestionRepos;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testquestion.service.TestQuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_031_AnswerManagement_Tests {

    @InjectMocks
    private TestQuestionServiceImpl testQuestionService;

    @Mock
    private TestQuestionRepos testQuestionRepository;

    @Mock
    private TestQuestionMapper testQuestionMapper;

    @Mock
    private QuestionGroupService questionGroupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateAnswer_Success() {
        // Arrange
        TestQuestionDTO request = new TestQuestionDTO(
                1L,
                TestQuestion.EType.MULTIPLE_CHOICE,
                2,
                "Updated Question",
                "Select all prime numbers.",
                List.of("2", "4", "5"),
                List.of("2", "5"),
                1L
        );

        TestQuestion existingTestQuestion = TestQuestion.builder()
                .id(1L)
                .type(TestQuestion.EType.SINGLE_CHOICE)
                .ordinalNumber(1)
                .title("Old Question")
                .description("What is 2 + 2?")
                .options(List.of("2", "3", "4"))
                .correctAnswers(List.of("4"))
                .build();

        TestQuestion updatedTestQuestion = TestQuestion.builder()
                .id(1L)
                .type(TestQuestion.EType.MULTIPLE_CHOICE)
                .ordinalNumber(2)
                .title("Updated Question")
                .description("Select all prime numbers.")
                .options(List.of("2", "4", "5"))
                .correctAnswers(List.of("2", "5"))
                .build();

        TestQuestionDTO expectedResponse = new TestQuestionDTO(
                1L,
                TestQuestion.EType.MULTIPLE_CHOICE,
                2,
                "Updated Question",
                "Select all prime numbers.",
                List.of("2", "4", "5"),
                List.of("2", "5"),
                1L
        );

        when(testQuestionRepository.findById(1L)).thenReturn(Optional.of(existingTestQuestion));
        when(testQuestionRepository.save(any(TestQuestion.class))).thenReturn(updatedTestQuestion);
        when(testQuestionMapper.toDTO(any(TestQuestion.class))).thenReturn(expectedResponse);

        // Act
        TestQuestionDTO result = testQuestionService.update(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Question", result.title());
        verify(testQuestionRepository, times(1)).save(existingTestQuestion);
    }




}
