package com.universityweb.enrollment;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/enrollments")
@RestController
@Tag(name = "Enrollments")
public class EnrollmentController extends BaseController<Enrollment, EnrollmentDTO, Long, EnrollmentService> {

    private AuthService authService;

    @Autowired
    public EnrollmentController(EnrollmentService service, AuthService authService) {
        super(service);
        this.authService = authService;
    }

    @GetMapping("/is-enrolled")
    public ResponseEntity<EnrollmentDTO> isEnrolled(Long courseId) {
        String username = authService.getCurrentUsername();
        EnrollmentDTO enrollmentDTO = service.isEnrolled(username, courseId);
        return ResponseEntity.ok(enrollmentDTO);
    }
}
