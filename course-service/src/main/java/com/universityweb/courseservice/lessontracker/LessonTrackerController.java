package com.universityweb.courseservice.lessontracker;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lessontracker.dto.LessonTrackerDTO;
import com.universityweb.lessontracker.service.LessonTrackerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/lesson-trackers")
@RestController
@Tag(name = "Lesson Trackers")
public class LessonTrackerController
        extends BaseController<LessonTracker, LessonTrackerDTO, Long, LessonTrackerService> {

    private final AuthService authService;

    @Autowired
    public LessonTrackerController(
            LessonTrackerService service,
            AuthService authService
    ) {
        super(service);
        this.authService = authService;
    }

    @GetMapping("/is-learned/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> isLearned(
            @PathVariable Long lessonId
    ) {
        String username = authService.getCurrentUsername();
        log.info("Checking if user '{}' has learned lesson '{}'", username, lessonId);

        Boolean isLearned = service.isLearned(username, lessonId);

        log.info("User '{}' has learned lesson '{}': {}", username, lessonId, isLearned);
        return ResponseEntity.ok(isLearned);
    }

    @GetMapping("/get-first-unlearned-lesson/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonResponse> getFirstUnlearnedLesson(
            @PathVariable Long courseId
    ) {
        String username = authService.getCurrentUsername();
        log.info("Getting first unlearned lesson for user '{}' and course '{}'", username, courseId);

        LessonResponse lessonResponse = service.getFirstUnlearnedLesson(username, courseId);

        log.info("First unlearned lesson for user '{}' and course '{}': {}", username, courseId, lessonResponse);
        return ResponseEntity.ok(lessonResponse);
    }
}
