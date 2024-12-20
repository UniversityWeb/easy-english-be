package com.universityweb.common.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.questiongroup.QuestionGroupMapper;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.service.QuestionGroupServiceImpl;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.service.TestPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_020_QuestionGroupManagement_Tests {

    @InjectMocks
    private QuestionGroupServiceImpl questionGroupService;

    @Mock
    private QuestionGroupRepos questionGroupRepository;

    @Mock
    private QuestionGroupMapper questionGroupMapper;

    @Mock
    private TestPartService testPartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQuestionGroup_Success() {
        // Arrange
        QuestionGroupDTO request = new QuestionGroupDTO(
                null,
                1,
                1,
                10,
                "Requirement Text",
                null,
                1L
        );

        TestPart testPart = TestPart.builder()
                .id(1L)
                .build();

        QuestionGroup questionGroupEntity = QuestionGroup.builder()
                .id(1L)
                .ordinalNumber(1)
                .from(1)
                .to(10)
                .requirement("Requirement Text")
                .testPart(testPart)
                .build();

        QuestionGroupDTO expectedResponse = new QuestionGroupDTO(
                1L,
                1,
                1,
                10,
                "Requirement Text",
                null,
                1L
        );

        when(testPartService.getEntityById(1L)).thenReturn(testPart);
        when(questionGroupMapper.toEntity(any(QuestionGroupDTO.class))).thenReturn(questionGroupEntity);
        when(questionGroupRepository.save(any(QuestionGroup.class))).thenReturn(questionGroupEntity);
        when(questionGroupMapper.toDTO(any(QuestionGroup.class))).thenReturn(expectedResponse);

        // Act
        QuestionGroupDTO result = questionGroupService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Requirement Text", result.getRequirement());
        verify(questionGroupRepository, times(1)).save(any(QuestionGroup.class));
    }

    @Test
    void testUpdateQuestionGroup_Success() {
        // Arrange
        QuestionGroupDTO request = new QuestionGroupDTO(
                1L,
                2,
                5,
                15,
                "Updated Requirement",
                null,
                1L
        );

        QuestionGroup existingQuestionGroup = QuestionGroup.builder()
                .id(1L)
                .ordinalNumber(1)
                .from(1)
                .to(10)
                .requirement("Old Requirement")
                .build();

        QuestionGroup updatedQuestionGroup = QuestionGroup.builder()
                .id(1L)
                .ordinalNumber(2)
                .from(5)
                .to(15)
                .requirement("Updated Requirement")
                .build();

        QuestionGroupDTO expectedResponse = new QuestionGroupDTO(
                1L,
                2,
                5,
                15,
                "Updated Requirement",
                null,
                1L
        );

        when(questionGroupRepository.findById(1L)).thenReturn(Optional.of(existingQuestionGroup));
        when(questionGroupRepository.save(any(QuestionGroup.class))).thenReturn(updatedQuestionGroup);
        when(questionGroupMapper.toDTO(any(QuestionGroup.class))).thenReturn(expectedResponse);

        // Act
        QuestionGroupDTO result = questionGroupService.update(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Requirement", result.getRequirement());
        verify(questionGroupRepository, times(1)).save(existingQuestionGroup);
    }

    @Test
    void testSoftDeleteQuestionGroup_Success() {
        // Arrange
        Long questionGroupId = 1L;
        QuestionGroup questionGroup = QuestionGroup.builder()
                .id(questionGroupId)
                .isDeleted(false)
                .build();

        when(questionGroupRepository.findById(questionGroupId)).thenReturn(Optional.of(questionGroup));

        // Act
        questionGroupService.softDelete(questionGroupId);

        // Assert
        assertTrue(questionGroup.getIsDeleted());
        verify(questionGroupRepository, times(1)).save(questionGroup);
    }


}
