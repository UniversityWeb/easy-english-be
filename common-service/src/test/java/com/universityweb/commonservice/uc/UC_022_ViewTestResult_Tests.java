package com.universityweb.commonservice.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.test.service.TestService;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.dto.TestResultWithoutListDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.mapper.TestResultMapper;
import com.universityweb.testresult.request.GetTestResultReq;
import com.universityweb.testresult.service.TestResultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_022_ViewTestResult_Tests {

    @InjectMocks
    private TestResultServiceImpl testResultService;

    @Mock
    private TestResultRepos testResultRepository;

    @Mock
    private TestResultMapper testResultMapper;

    @Mock
    private TestService testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTestResultById_Success() {
        // Arrange
        TestResult testResult = TestResult.builder()
                .id(1L)
                .result("Passed")
                .correctPercent(85.0)
                .status(TestResult.EStatus.DONE)
                .takingDuration(3600)
                .build();

        TestResultDTO expectedDTO = new TestResultDTO(
                1L,
                "Passed",
                85.0,
                TestResult.EStatus.DONE,
                3600,
                null,
                null,
                "user1",
                1L,
                null,
                null,
                null
        );

        when(testResultRepository.findById(1L)).thenReturn(Optional.of(testResult));
        when(testResultMapper.toDTO(testResult)).thenReturn(expectedDTO);

        // Act
        TestResultDTO result = testResultService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Passed", result.getResult());
        verify(testResultRepository, times(1)).findById(1L);
    }


    @Test
    void testGetTestHistoryByTestId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        TestResult testResult = TestResult.builder()
                .id(1L)
                .result("Passed")
                .correctPercent(85.0)
                .status(TestResult.EStatus.DONE)
                .build();

        TestResultWithoutListDTO dto = new TestResultWithoutListDTO(
                1L,
                "Passed",
                85.0,
                TestResult.EStatus.DONE,
                3600,
                null,
                null,
                "user1",
                1L,
                null
        );

        Page<TestResult> testResultPage = new PageImpl<>(Collections.singletonList(testResult));

        when(testResultRepository.findByTest_IdOrderByFinishedAtDesc(1L, pageable)).thenReturn(testResultPage);
        when(testResultMapper.toTestResultWithoutListDTO(any(TestResult.class))).thenReturn(dto);

        // Mock testService.getCourseIdByTestId nếu cần thiết
        when(testService.getCourseIdByTestId(1L)).thenReturn(101L);  // Trả về courseId giả

        // Act
        Page<TestResultWithoutListDTO> result = testResultService.getTestHistoryByTestId(new GetTestResultReq(1L, 0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Passed", result.getContent().get(0).getResult());
        verify(testResultRepository, times(1)).findByTest_IdOrderByFinishedAtDesc(1L, pageable);
    }

}
