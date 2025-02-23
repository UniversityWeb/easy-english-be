package com.universityweb.userservice.favourite;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.favourite.dto.FavouriteDTO;
import com.universityweb.favourite.entity.Favourite;
import com.universityweb.favourite.service.FavouriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/api/v1/favourites")
@RestController
@Tag(name = "Favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private Logger log = LogManager.getLogger(FavouriteController.class);

    private final FavouriteService favouriteService;
    private final MediaService mediaService;
    private final AuthService authService;

    @PostMapping("/add/{courseId}")
    public ResponseEntity<FavouriteDTO> add(@PathVariable Long courseId) {
        String username = authService.getCurrentUsername();
        log.info("Attempting to add course {} to favourites " +
                "for user {}", courseId, username);

        FavouriteDTO favouriteDTO = FavouriteDTO.builder()
                .createdAt(LocalDateTime.now())
                .courseId(courseId)
                .username(username)
                .build();

        FavouriteDTO saved = favouriteService.create(favouriteDTO);
        log.info("Course {} added to favourites " +
                "for user {}", courseId, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId) {
        String username = authService.getCurrentUsername();
        log.info("Attempting to remove course {} from favourites " +
                "for user {}", courseId, username);

        Favourite favourite = favouriteService.getByUsernameAndCourseId(username, courseId);
        favouriteService.softDelete(favourite.getId());
        log.info("Course {} removed from favourites " +
                "for user {}", courseId, username);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAllFavorites(
            @RequestParam int page,
            @RequestParam int size
    ) {
        String username = authService.getCurrentUsername();
        log.info("Fetching all favourite courses for user {} " +
                "with pagination (page: {}, size: {})", username, page, size);

        Page<CourseResponse> courseResponses = favouriteService
                .getAllFavorites(username, page, size);
        log.info("Fetched {} favourite courses for user {}",
                courseResponses.getTotalElements(), username);

        return ResponseEntity
                .ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<CourseResponse>> getAllFavoritesByFilter(
            @RequestBody CourseRequest courseRequest
    ) {
        String username = authService.getCurrentUsername();
        courseRequest.setUsername(username);
        log.info("Fetching favourite courses for user {} with filter: {}",
                username, courseRequest);

        Page<CourseResponse> courseResponses = favouriteService
                .getAllFavoritesByFilter(courseRequest);
        log.info("Fetched {} favourite courses for user {} with applied filter",
                courseResponses.getTotalElements(), username);

        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/check-course-in-favorite/{courseId}")
    public ResponseEntity<Boolean> checkCourseInFavorite(
            @PathVariable Long courseId
    ) {
        String username = authService.getCurrentUsername();
        Boolean isValid = favouriteService.checkCourseInFavorite(username, courseId);
        return ResponseEntity.ok(isValid);
    }
}
