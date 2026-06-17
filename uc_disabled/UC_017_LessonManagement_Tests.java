package com.universityweb.common.uc;

import com.universityweb.lesson.LessonRepository;
import com.universityweb.lesson.customenum.LessonType;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.mapper.LessonMapper;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.service.LessonServiceImpl;
import com.universityweb.section.entity.Section;
import com.universityweb.section.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_017_LessonManagement_Tests {

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private SectionService sectionService;

    @Mock
    private LessonMapper lessonMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLesson_Success() {
        // Arrange
        LessonRequest request = LessonRequest.builder()
                .id(null)
                .sectionId(1L)
                .title("New Lesson")
                .type("VIDEO")
                .content("Lesson Content")
                .contentUrl("http://example.com/video")
                .description("Lesson Description")
                .duration(60)
                .isPreview(true)
                .build();

        Section section = Section.builder()
                .id(1L)
                .build();

        Lesson lesson = Lesson.builder()
                .id(1L)
                .title("New Lesson")
                .type(LessonType.VIDEO)
                .content("Lesson Content")
                .contentUrl("http://example.com/video")
                .description("Lesson Description")
                .duration(60)
                .isPreview(true)
                .section(section)
                .build();

        LessonResponse expectedResponse = LessonResponse.builder()
                .id(1L)
                .sectionId(1L)
                .title("New Lesson")
                .type(LessonType.VIDEO)
                .content("Lesson Content")
                .contentUrl("http://example.com/video")
                .description("Lesson Description")
                .duration(60)
                .isPreview(true)
                .build();

        when(sectionService.getEntityById(1L)).thenReturn(section);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);
        when(lessonMapper.toDTO(any(Lesson.class))).thenReturn(expectedResponse);

        // Act
        LessonResponse result = lessonService.createLesson(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getTitle(), result.getTitle());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson_Success() {
        // Arrange
        LessonRequest request = LessonRequest.builder()
                .id(1L)
                .title("Updated Lesson")
                .type("VIDEO")
                .content("Updated Content")
                .contentUrl("http://example.com/updated-video")
                .description("Updated Description")
                .duration(90)
                .isPreview(false)
                .sectionId(1L)  // Ensure sectionId is provided
                .build();

        // Mocking the existing Lesson object
        Section existingSection = Section.builder()
                .id(1L)
                .title("Section 1")
                .build();

        Lesson existingLesson = Lesson.builder()
                .id(1L)
                .title("Old Lesson")
                .content("Old Content")
                .section(existingSection)  // Ensure section is assigned
                .build();

        // Updated Lesson
        Lesson updatedLesson = Lesson.builder()
                .id(1L)
                .title("Updated Lesson")
                .type(LessonType.VIDEO)
                .content("Updated Content")
                .contentUrl("http://example.com/updated-video")
                .description("Updated Description")
                .duration(90)
                .isPreview(false)
                .section(existingSection)  // Ensure section is assigned correctly
                .build();

        LessonResponse expectedResponse = LessonResponse.builder()
                .id(1L)
                .title("Updated Lesson")
                .type(LessonType.VIDEO)
                .content("Updated Content")
                .contentUrl("http://example.com/updated-video")
                .description("Updated Description")
                .duration(90)
                .isPreview(false)
                .build();

        // Mocks for the service
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(updatedLesson);
        when(lessonMapper.toDTO(any(Lesson.class))).thenReturn(expectedResponse);

        // Act
        LessonResponse result = lessonService.updateLesson(request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Lesson", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        assertEquals("http://example.com/updated-video", result.getContentUrl());
        verify(lessonRepository, times(1)).save(any(Lesson.class));  // Verify save with any updated lesson
    }

    @Test
    void testSoftDeleteLesson_Success() {
        // Arrange
        Long lessonId = 1L;
        Lesson lesson = Lesson.builder()
                .id(lessonId)
                .isDeleted(false)
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // Act
        lessonService.delete(lessonId);

        // Assert
        assertTrue(lesson.getIsDeleted());
        verify(lessonRepository, times(1)).save(lesson);
    }


}
