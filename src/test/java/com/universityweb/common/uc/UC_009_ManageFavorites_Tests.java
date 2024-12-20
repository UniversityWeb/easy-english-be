package com.universityweb.common.uc;

import com.universityweb.course.response.CourseResponse;
import com.universityweb.favourite.FavouriteMapper;
import com.universityweb.favourite.dto.FavouriteDTO;
import com.universityweb.favourite.entity.Favourite;
import com.universityweb.favourite.repository.FavouriteRepository;
import com.universityweb.favourite.service.FavouriteService;
import com.universityweb.favourite.service.FavouriteServiceImpl;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_009_ManageFavorites_Tests {

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private FavouriteMapper favouriteMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFavourite_Success_MANAGE_FAVORITES_POS_001() {
        // Arrange
        String username = "john_doe";
        Long courseId = 1L;

        LocalDateTime now = LocalDateTime.now();

        FavouriteDTO favouriteDTO = FavouriteDTO.builder()
                .username(username)
                .courseId(courseId)
                .createdAt(now)
                .build();

        User user = new User();
        user.setUsername(username);

        Course course = new Course();
        course.setId(courseId);

        Favourite favourite = Favourite.builder()
                .isDeleted(false)
                .createdAt(now)
                .build();

        favourite.setUser(user);
        favourite.setCourse(course);

        when(favouriteMapper.toEntity(favouriteDTO)).thenReturn(favourite);
        when(favouriteMapper.toDTO(favourite)).thenReturn(favouriteDTO);
        when(userService.loadUserByUsername(username)).thenReturn(user);
        when(courseService.getEntityById(courseId)).thenReturn(course);
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);

        // Act
        FavouriteDTO result = favouriteService.create(favouriteDTO);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(courseId, result.getCourseId());
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    void testGetAllFavorites_Success_MANAGE_FAVORITES_POS_002() {
        // Arrange
        String username = "john_doe";
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Course course = new Course();
        course.setId(1L);
        course.setTitle("Java Programming");

        Favourite favourite = Favourite.builder()
                .id(1L)
                .user(User.builder().username(username).build())
                .course(course)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Page<Favourite> favouritesPage = new PageImpl<>(List.of(favourite));

        CourseResponse courseResponse = CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .build();

        when(favouriteRepository.findByUser_UsernameAndIsDeletedFalse(username, pageable))
                .thenReturn(favouritesPage);
        when(courseService.mapCourseToResponse(course)).thenReturn(courseResponse);

        // Act
        Page<CourseResponse> result = favouriteService.getAllFavorites(username, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Java Programming", result.getContent().get(0).getTitle());
        verify(favouriteRepository, times(1)).findByUser_UsernameAndIsDeletedFalse(username, pageable);
        verify(courseService, times(1)).mapCourseToResponse(course);
    }

    @Test
    void testSoftDeleteFavourite_Success_MANAGE_FAVORITES_POS_003() {
        // Arrange
        Long favouriteId = 1L;

        Favourite favourite = Favourite.builder()
                .id(favouriteId)
                .isDeleted(false)
                .build();

        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));

        // Act
        favouriteService.softDelete(favouriteId);

        // Assert
        assertTrue(favourite.getIsDeleted());
        verify(favouriteRepository, times(1)).save(favourite);
    }
}
