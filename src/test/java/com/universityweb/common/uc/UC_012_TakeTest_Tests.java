package com.universityweb.common.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.test.service.TestService;
import com.universityweb.testquestion.service.TestQuestionService;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.request.SubmitTestRequest;
import com.universityweb.testresult.service.TestResultServiceImpl;
import com.universityweb.test.entity.Test;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.useranswer.UserAnswerRepos;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_012_TakeTest_Tests {

    @InjectMocks
    private TestResultServiceImpl testResultService;

    @Mock
    private UserService userService;

    @Mock
    private TestService testService;

    @Mock
    private TestQuestionService testQuestionService;

    @Mock
    private UserAnswerRepos userAnswerRepos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.Test
    void testSubmitTest_Success_TAKE_TEST_POS_001() {
        // Arrange
        String username = "john_doe";
        Long testId = 1L;
        SubmitTestRequest.UserAnswerDTO answerDTO1 = new SubmitTestRequest.UserAnswerDTO(List.of("A"), 101L);
        SubmitTestRequest.UserAnswerDTO answerDTO2 = new SubmitTestRequest.UserAnswerDTO(List.of("B"), 102L);

        SubmitTestRequest request = SubmitTestRequest.builder()
                .testId(testId)
                .userAnswers(List.of(answerDTO1, answerDTO2))
                .startedAt(LocalDateTime.now().minusMinutes(30))
                .finishedAt(LocalDateTime.now())
                .takingDuration(30)
                .build();

        User user = User.builder()
                .username(username)
                .build();

        Test test = Test.builder()
                .id(testId)
                .passingGrade(0.0)
                .build();

        TestResult testResult = TestResult.builder()
                .id(1L)
                .status(TestResult.EStatus.IN_PROGRESS)
                .build();

        TestQuestion question1 = TestQuestion.builder()
                .id(101L)
                .type(TestQuestion.EType.SINGLE_CHOICE)
                .ordinalNumber(1)
                .title("Question 1")
                .description("What is Java?")
                .options(List.of("A", "B", "C"))
                .correctAnswers(List.of("A"))
                .build();

        TestQuestion question2 = TestQuestion.builder()
                .id(102L)
                .type(TestQuestion.EType.SINGLE_CHOICE)
                .ordinalNumber(2)
                .title("Question 2")
                .description("What is OOP?")
                .options(List.of("A", "B", "C"))
                .correctAnswers(List.of("B"))
                .build();

        when(userService.loadUserByUsername(username)).thenReturn(user);
        when(testService.getEntityById(testId)).thenReturn(test);
        when(testQuestionService.findByTestId(testId)).thenReturn(List.of(question1, question2));
        when(testQuestionService.getNumberOfQuestions(testId)).thenReturn(2);
        when(userAnswerRepos.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        TestResultDTO result = testResultService.submit(username, request);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, result.getCorrectPercent());
        assertEquals(TestResult.EStatus.DONE, result.getStatus());
        verify(userAnswerRepos, times(1)).saveAll(anyList());
    }

    @org.junit.jupiter.api.Test
    void testSubmitTest_NoAnswers_TAKE_TEST_NEG_001() {
        // Arrange
        String username = "john_doe";
        SubmitTestRequest request = SubmitTestRequest.builder()
                .testId(1L)
                .userAnswers(new ArrayList<>())
                .build();

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> testResultService.submit(username, request)
        );

        assertEquals("Could not find any user answers", exception.getMessage());
        verify(userAnswerRepos, never()).saveAll(anyList());
    }
}
