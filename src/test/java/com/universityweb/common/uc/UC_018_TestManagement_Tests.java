package com.universityweb.common.uc;

import com.universityweb.section.entity.Section;
import com.universityweb.section.service.SectionService;
import com.universityweb.test.TestMapper;
import com.universityweb.test.TestRepos;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_018_TestManagement_Tests {

    @InjectMocks
    private TestServiceImpl testService;

    @Mock
    private TestRepos testRepository;

    @Mock
    private TestMapper testMapper;

    @Mock
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @org.junit.jupiter.api.Test
    void testCreateTest_Success() {
        // Arrange
        TestDTO request = TestDTO.builder()
                .id(null)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DRAFT)
                .title("New Test")
                .description("Test Description")
                .durationInMilis(3600000)
                .passingGrade(75.0)
                .build();

        Test testEntity = Test.builder()
                .id(1L)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DRAFT)
                .title("New Test")
                .description("Test Description")
                .durationInMilis(3600000)
                .passingGrade(75.0)
                .build();

        TestDTO expectedResponse = TestDTO.builder()
                .id(1L)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DRAFT)
                .title("New Test")
                .description("Test Description")
                .durationInMilis(3600000)
                .passingGrade(75.0)
                .build();

        // Khi gọi sectionService.getEntityById, mock kết quả trả về nếu cần
        when(sectionService.getEntityById(any())).thenReturn(new Section());  // Mock trả về Section nếu cần

        when(testMapper.toEntity(any(TestDTO.class))).thenReturn(testEntity);
        when(testRepository.save(any(Test.class))).thenReturn(testEntity);
        when(testMapper.toDTO(any(Test.class))).thenReturn(expectedResponse);

        // Act
        TestDTO result = testService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("New Test", result.getTitle());
        verify(testRepository, times(1)).save(any(Test.class));  // Xác nhận save được gọi 1 lần
    }

    @org.junit.jupiter.api.Test
    void testUpdateTest_Success() {
        // Arrange
        TestDTO request = TestDTO.builder()
                .id(1L)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DISPLAY)
                .title("Updated Test")
                .description("Updated Description")
                .durationInMilis(3600000)
                .passingGrade(80.0)
                .build();

        Test existingTest = Test.builder()
                .id(1L)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DRAFT)
                .title("Old Test")
                .description("Old Description")
                .durationInMilis(3600000)
                .passingGrade(70.0)
                .build();

        Test updatedTest = Test.builder()
                .id(1L)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DISPLAY)
                .title("Updated Test")
                .description("Updated Description")
                .durationInMilis(3600000)
                .passingGrade(80.0)
                .build();

        TestDTO expectedResponse = TestDTO.builder()
                .id(1L)
                .type(Test.EType.QUIZ)
                .status(Test.EStatus.DISPLAY)
                .title("Updated Test")
                .description("Updated Description")
                .durationInMilis(3600000)
                .passingGrade(80.0)
                .build();

        when(testRepository.findById(1L)).thenReturn(Optional.of(existingTest));
        when(testRepository.save(any(Test.class))).thenReturn(updatedTest);
        when(testMapper.toDTO(any(Test.class))).thenReturn(expectedResponse);

        // Act
        TestDTO result = testService.update(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Test", result.getTitle());
        verify(testRepository, times(1)).save(existingTest);
    }

    @org.junit.jupiter.api.Test
    void testSoftDeleteTest_Success() {
        // Arrange
        Long testId = 1L;
        Test test = Test.builder()
                .id(testId)
                .status(Test.EStatus.DISPLAY)
                .build();

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // Act
        testService.delete(testId);

        // Assert
        assertEquals(Test.EStatus.DELETED, test.getStatus());
        verify(testRepository, times(1)).save(test);
    }


}
