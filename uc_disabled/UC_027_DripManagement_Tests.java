package com.universityweb.common.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.drip.Drip;
import com.universityweb.drip.DripMapper;
import com.universityweb.drip.DripRepos;
import com.universityweb.drip.dto.DripDTO;
import com.universityweb.drip.dto.DripsOfPrevDTO;
import com.universityweb.drip.service.DripServiceImpl;
import com.universityweb.lesson.customenum.LessonType;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.test.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_027_DripManagement_Tests {

    @InjectMocks
    private DripServiceImpl dripService;

    @Mock
    private DripRepos dripRepository;

    @Mock
    private DripMapper dripMapper;

    @Mock
    private CourseService courseService;

    @Mock
    private LessonService lessonService;

    @Mock
    private TestService testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateDrips_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = Course.builder().id(courseId).build();

        DripsOfPrevDTO dripUpdateRequest = DripsOfPrevDTO.builder()
                .prevId(1L)
                .prevType(Drip.ESourceType.LESSON)
                .nextDrips(Collections.singletonList(
                        DripsOfPrevDTO.DripOfPrevDTO.builder()
                                .nextId(2L)
                                .nextType(Drip.ESourceType.TEST)
                                .build()
                ))
                .build();

        when(courseService.getEntityById(courseId)).thenReturn(course);
        when(dripRepository.saveAll(any())).thenReturn(Collections.emptyList());

        // Act
        List<DripsOfPrevDTO> result = dripService.updateDrips(courseId, Collections.singletonList(dripUpdateRequest));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dripRepository, times(1)).deleteByCourseId(courseId);
        verify(dripRepository, times(1)).saveAll(any());
    }

    @Test
    void testGetAllDripsByCourseId_Success() {
        // Arrange
        Long courseId = 1L;

        Drip drip1 = Drip.builder()
                .prevId(1L)
                .prevType(Drip.ESourceType.LESSON)
                .nextId(2L)
                .nextType(Drip.ESourceType.TEST)
                .course(Course.builder().id(courseId).build())
                .build();

        Drip drip2 = Drip.builder()
                .prevId(3L)
                .prevType(Drip.ESourceType.TEST)
                .nextId(4L)
                .nextType(Drip.ESourceType.LESSON)
                .course(Course.builder().id(courseId).build())
                .build();

        // Create a mock Lesson object with a valid LessonType
        Lesson mockLesson = new Lesson();
        mockLesson.setType(LessonType.VIDEO); // Set the type to a valid LessonType

        com.universityweb.test.entity.Test mockTest = new com.universityweb.test.entity.Test();
        mockTest.setType(com.universityweb.test.entity.Test.EType.CUSTOM);
        // Mock repository and lesson service
        when(dripRepository.findAllByCourseId(courseId)).thenReturn(Arrays.asList(drip1, drip2));

        // Mock lessonService method to return the mockLesson
        when(lessonService.getEntityById(any())).thenReturn(mockLesson);
        when(testService.getEntityById(any())).thenReturn(mockTest);

        when(dripMapper.toDTO(any(Drip.class))).thenAnswer(invocation -> {
            Drip drip = invocation.getArgument(0);
            return DripsOfPrevDTO.builder()
                    .prevId(drip.getPrevId())
                    .prevType(drip.getPrevType())
                    .nextDrips(Collections.singletonList(
                            DripsOfPrevDTO.DripOfPrevDTO.builder()
                                    .nextId(drip.getNextId())
                                    .nextType(drip.getNextType())
                                    .build()
                    ))
                    .build();
        });

        // Act
        List<DripsOfPrevDTO> result = dripService.getAllDripsByCourseId(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dripRepository, times(1)).findAllByCourseId(courseId);
        verify(lessonService, times(1)).getEntityById(any());  // Verify lessonService method is called
    }

}
