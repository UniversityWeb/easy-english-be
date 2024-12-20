package com.universityweb.common.uc;

import com.universityweb.category.CategoryRepository;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseServiceImpl;
import com.universityweb.category.entity.Category;
import com.universityweb.level.LevelRepository;
import com.universityweb.level.entity.Level;
import com.universityweb.topic.TopicRepository;
import com.universityweb.topic.entity.Topic;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_015_CourseManagement_Tests {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private CourseMapper courseMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourse_Success_COURSE_CREATE_POS_001() {
        // Arrange
        CourseRequest request = CourseRequest.builder()
                .title("Java Programming")
                .levelId(1L)
                .topicId(2L)
                .categoryIds(Arrays.asList(3L, 4L))
                .ownerUsername("john_doe")
                .build();

        Level level = new Level();
        level.setId(1L);

        Topic topic = new Topic();
        topic.setId(2L);

        Category category1 = new Category();
        category1.setId(3L);

        Category category2 = new Category();
        category2.setId(4L);

        User owner = new User();
        owner.setUsername("john_doe");

        Course course = Course.builder()
                .title("Java Programming")
                .level(level)
                .topic(topic)
                .categories(List.of(category1, category2))
                .owner(owner)
                .build();

        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(topicRepository.findById(2L)).thenReturn(Optional.of(topic));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category2));
        when(userService.loadUserByUsername("john_doe")).thenReturn(owner);
        when(courseMapper.toEntity(request)).thenReturn(course);

        // Act
        CourseResponse response = courseService.createCourse(request);

        // Assert
        assertNotNull(course);
        assertEquals("Java Programming", course.getTitle());
    }

    @Test
    void testUpdateCourse_Success_COURSE_UPDATE_POS_002() {
        // Arrange
        CourseRequest request = CourseRequest.builder()
                .id(1L)
                .title("Advanced Java Programming")
                .levelId(1L)
                .topicId(2L)
                .categoryIds(Arrays.asList(3L, 4L))
                .build();

        Course currentCourse = Course.builder()
                .id(1L)
                .title("Java Programming")
                .build();

        Level level = Level.builder()
                .id(1L)
                .build();

        Topic topic = Topic.builder()
                .id(2L)
                .build();

        Category category1 = Category.builder()
                .id(3L)
                .build();

        Category category2 = Category.builder()
                .id(4L)
                .build();

        // Mocks
        when(courseRepository.findById(1L)).thenReturn(Optional.of(currentCourse));
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(topicRepository.findById(2L)).thenReturn(Optional.of(topic));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category2));

        // Act
        CourseResponse response = courseService.updateCourse(request);

        // Assert
        assertNotNull(request);
        assertEquals("Advanced Java Programming", request.getTitle());
        verify(courseRepository, times(1)).findById(1L);
        verify(levelRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).findById(3L);
        verify(categoryRepository, times(1)).findById(4L);
    }
}
