package com.universityweb.common.uc;

import com.universityweb.course.service.CourseService;
import com.universityweb.section.SectionRepository;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.mapper.SectionMapper;
import com.universityweb.section.request.SectionRequest;
import com.universityweb.section.service.SectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_016_SectionManagement_Tests {

    @InjectMocks
    private SectionServiceImpl sectionService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionMapper sectionMapper;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSection_Success() {
        // Arrange
        SectionRequest request = SectionRequest.builder()
                .id(null)
                .status(Section.EStatus.DISPLAY)
                .title("New Section")
                .createdAt("2024-12-20")
                .updatedAt(null)
                .courseId(1L)
                .build();

        Section section = Section.builder()
                .id(null)
                .status(Section.EStatus.DISPLAY)
                .title("New Section")
                .createdAt("2024-12-20")
                .updatedAt(null)
                .build();

        SectionDTO expectedDto = SectionDTO.builder()
                .id(1L)
                .status(Section.EStatus.DISPLAY)
                .title("New Section")
                .createdAt("2024-12-20")
                .updatedAt(null)
                .courseId(1L)
                .build();

        when(sectionMapper.toDTO(request)).thenReturn(expectedDto);
        when(sectionRepository.save(any(Section.class))).thenReturn(section);
        when(sectionMapper.toDTO(any(Section.class))).thenReturn(expectedDto);

        // Act
        SectionDTO result = sectionService.createSection(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getTitle(), result.getTitle());
        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void testUpdateSection_Success() {
        // Arrange
        SectionRequest request = SectionRequest.builder()
                .id(1L)
                .status(Section.EStatus.DISPLAY)
                .title("Updated Section")
                .createdAt("2024-12-20")
                .updatedAt("2024-12-21")
                .courseId(1L)
                .build();

        Section existingSection = Section.builder()
                .id(1L)
                .status(Section.EStatus.DISPLAY)
                .title("Old Section")
                .createdAt("2024-12-19")
                .updatedAt(null)
                .build();

        Section updatedSection = Section.builder()
                .id(1L)
                .status(Section.EStatus.DISPLAY)
                .title("Updated Section")
                .createdAt("2024-12-20")
                .updatedAt("2024-12-21")
                .build();

        SectionDTO expectedDto = SectionDTO.builder()
                .id(1L)
                .status(Section.EStatus.DISPLAY)
                .title("Updated Section")
                .createdAt("2024-12-20")
                .updatedAt("2024-12-21")
                .courseId(1L)
                .build();

        when(sectionRepository.findById(1L)).thenReturn(Optional.of(existingSection));
        when(sectionRepository.save(existingSection)).thenReturn(updatedSection);
        when(sectionMapper.toDTO(updatedSection)).thenReturn(expectedDto);

        // Act
        SectionDTO result = sectionService.updateSection(request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Section", result.getTitle());
        verify(sectionRepository, times(1)).save(existingSection);
    }

    @Test
    void testSoftDelete_Success() {
        // Arrange
        Long sectionId = 1L;
        Section section = Section.builder()
                .id(sectionId)
                .status(Section.EStatus.DISPLAY)
                .title("Section to delete")
                .createdAt("2024-12-19")
                .updatedAt(null)
                .build();

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));

        // Act
        sectionService.delete(sectionId);

        // Assert
        assertEquals(Section.EStatus.DELETED, section.getStatus());
        verify(sectionRepository, times(1)).save(section);
    }

}
