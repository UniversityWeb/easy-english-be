package com.universityweb.commonservice.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testpart.TestPartRepos;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.mapper.TestPartMapper;
import com.universityweb.testpart.service.TestPartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_019_TestPartManagement_Test {

    @InjectMocks
    private TestPartServiceImpl testPartService;

    @Mock
    private TestPartRepos testPartRepository;

    @Mock
    private TestPartMapper testPartMapper;

    @Mock
    private TestService testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.Test
    void testCreateTestPart_Success() {
        // Arrange
        TestPartDTO request = new TestPartDTO(
                null,
                "New Test Part",
                "Reading Passage",
                1,
                1L,
                null
        );

        Test test = Test.builder()
                .id(1L)
                .build();

        TestPart testPartEntity = TestPart.builder()
                .id(1L)
                .title("New Test Part")
                .readingPassage("Reading Passage")
                .ordinalNumber(1)
                .test(test)
                .build();

        TestPartDTO expectedResponse = new TestPartDTO(
                1L,
                "New Test Part",
                "Reading Passage",
                1,
                1L,
                null
        );

        when(testService.getEntityById(1L)).thenReturn(test);
        when(testPartMapper.toEntity(any(TestPartDTO.class))).thenReturn(testPartEntity);
        when(testPartRepository.save(any(TestPart.class))).thenReturn(testPartEntity);
        when(testPartMapper.toDTO(any(TestPart.class))).thenReturn(expectedResponse);

        // Act
        TestPartDTO result = testPartService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("New Test Part", result.title());
        verify(testPartRepository, times(1)).save(any(TestPart.class));
    }

    @org.junit.jupiter.api.Test
    void testUpdateTestPart_Success() {
        // Arrange
        TestPartDTO request = new TestPartDTO(
                1L,
                "Updated Test Part",
                "Updated Reading Passage",
                2,
                1L,
                null
        );

        TestPart existingTestPart = TestPart.builder()
                .id(1L)
                .title("Old Test Part")
                .readingPassage("Old Reading Passage")
                .ordinalNumber(1)
                .build();

        TestPart updatedTestPart = TestPart.builder()
                .id(1L)
                .title("Updated Test Part")
                .readingPassage("Updated Reading Passage")
                .ordinalNumber(2)
                .build();

        TestPartDTO expectedResponse = new TestPartDTO(
                1L,
                "Updated Test Part",
                "Updated Reading Passage",
                2,
                1L,
                null
        );

        when(testPartRepository.findById(1L)).thenReturn(Optional.of(existingTestPart));
        when(testPartRepository.save(any(TestPart.class))).thenReturn(updatedTestPart);
        when(testPartMapper.toDTO(any(TestPart.class))).thenReturn(expectedResponse);

        // Act
        TestPartDTO result = testPartService.update(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Test Part", result.title());
        verify(testPartRepository, times(1)).save(existingTestPart);
    }

    @org.junit.jupiter.api.Test
    void testSoftDeleteTestPart_Success() {
        // Arrange
        Long testPartId = 1L;
        TestPart testPart = TestPart.builder()
                .id(testPartId)
                .isDeleted(false)
                .build();

        when(testPartRepository.findById(testPartId)).thenReturn(Optional.of(testPart));

        // Act
        testPartService.softDelete(testPartId);

        // Assert
        assertTrue(testPart.getIsDeleted());
        verify(testPartRepository, times(1)).save(testPart);
    }

}
