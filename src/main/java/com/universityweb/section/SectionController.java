package com.universityweb.section;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.util.Utils;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.service.EnrollmentService;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.lessontracker.service.LessonTrackerService;
import com.universityweb.section.customenum.ESectionItemType;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.dto.SectionItemDTO;
import com.universityweb.section.request.SectionRequest;
import com.universityweb.section.service.SectionService;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.service.TestService;
import com.universityweb.testresult.service.TestResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/section")
@RestController
@Tag(name = "Course sections")
@RequiredArgsConstructor
public class SectionController {

    private static final Logger log = LogManager.getLogger(SectionController.class);

    private final AuthService authService;
    private final SectionService sectionService;
    private final EnrollmentService enrollmentService;
    private final LessonService lessonService;
    private final TestService testService;
    private final LessonTrackerService lessonTrackerService;
    private final TestResultService testResultService;

    @PostMapping("/create-section")
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionRequest sectionRequest) {
        return  ResponseEntity.ok().body(sectionService.createSection(sectionRequest));
    }

    @PostMapping("/update-section")
    public ResponseEntity<SectionDTO> updateSection(@RequestBody SectionRequest sectionRequest) {
        return  ResponseEntity.ok().body(sectionService.updateSection(sectionRequest));
    }
    @PostMapping("/delete-section")
    public String deleteSection(@RequestBody SectionRequest sectionRequest) {
        sectionService.delete(sectionRequest.getId());
        return "Section deleted successfully";
    }

    @PostMapping("/get-all-section-by-course")
    public ResponseEntity<List<SectionDTO>> getAllSectionByCourse(@RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok(sectionService.getAllSectionByCourse(sectionRequest));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/get-first-unfinished-item/{courseId}")
    public ResponseEntity<SectionItemDTO> getFirstUnfinishedItem(
            @PathVariable Long courseId
    ) {
        String username = authService.getCurrentUsername();
        EnrollmentDTO enrollmentDTO = enrollmentService.isEnrolled(username, courseId);
        if (enrollmentDTO == null) {
            throw new CustomException("Course must be enrolled by the user");
        }

        // get all section
        List<SectionDTO> sectionDTOs = sectionService.getAllSectionByCourse(courseId);
        for (SectionDTO sectionDTO : sectionDTOs) {
            Long sectionId = sectionDTO.getId();

            List<LessonResponse> lessonResponses = lessonService.getAllLessonBySection(username, sectionId);
            for (LessonResponse lessonResponse : lessonResponses) {
                Long lessonId = lessonResponse.getId();
                boolean isLearned = lessonTrackerService.isLearned(username, lessonId);
                if (!isLearned) {
                    String lessonType = lessonResponse.getType().toString();
                    SectionItemDTO sectionItemDTO = new SectionItemDTO(Utils.safeEnumConversion(ESectionItemType.class, lessonType).toString(), lessonId);
                    return ResponseEntity.ok(sectionItemDTO);
                }
            }

            List<TestDTO> testDTOs = testService.getBySection(username, sectionId);
            for (TestDTO testDTO : testDTOs) {
                Long testId = testDTO.getId();
                boolean isDone = testResultService.isDone(username, testId);
                if (!isDone) {
                    SectionItemDTO sectionItemDTO = new SectionItemDTO(ESectionItemType.TEST.toString(), testId);
                    return ResponseEntity.ok(sectionItemDTO);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
