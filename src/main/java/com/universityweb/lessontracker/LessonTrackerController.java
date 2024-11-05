package com.universityweb.lessontracker;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.lessontracker.dto.LessonTrackerDTO;
import com.universityweb.lessontracker.service.LessonTrackerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
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
    public ResponseEntity<Boolean> isLearned(
            @PathVariable Long lessonId
    ) {
        String username = authService.getCurrentUsername();
        Boolean isLearned = service.isLearned(username, lessonId);
        return ResponseEntity.ok(isLearned);
    }
}
