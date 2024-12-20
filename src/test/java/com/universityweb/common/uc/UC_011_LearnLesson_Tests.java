package com.universityweb.common.uc;

import com.universityweb.course.entity.Course;
import com.universityweb.section.SectionRepository;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.request.SectionRequest;
import com.universityweb.section.service.SectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_011_LearnLesson_Tests {

    @InjectMocks
    private SectionServiceImpl sectionService;

    @Mock
    private SectionRepository sectionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSectionByCourse_Success_LEARN_LESSON_POS_001() {
        // Arrange
        Long courseId = 1L;
        SectionRequest sectionRequest = SectionRequest.builder()
                .courseId(courseId)
                .build();

        Section section1 = Section.builder()
                .id(1L)
                .title("Introduction to Java")
                .status(Section.EStatus.DISPLAY)
                .course(Course.builder().id(courseId).build())
                .build();

        Section section2 = Section.builder()
                .id(2L)
                .title("Advanced Java")
                .status(Section.EStatus.DISPLAY)
                .course(Course.builder().id(courseId).build())
                .build();

        List<Section> sections = List.of(section1, section2);

        when(sectionRepository.findByCourseId(courseId)).thenReturn(sections);

        // Act
        List<SectionDTO> result = sectionService.getAllSectionByCourse(sectionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Introduction to Java", result.get(0).getTitle());
        assertEquals("Advanced Java", result.get(1).getTitle());
        verify(sectionRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void testGetAllSectionByCourse_NoSections_LEARN_LESSON_NEG_001() {
        // Arrange
        Long courseId = 1L;
        SectionRequest sectionRequest = SectionRequest.builder()
                .courseId(courseId)
                .build();

        when(sectionRepository.findByCourseId(courseId)).thenReturn(List.of());

        // Act
        List<SectionDTO> result = sectionService.getAllSectionByCourse(sectionRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(sectionRepository, times(1)).findByCourseId(courseId);
    }
}
