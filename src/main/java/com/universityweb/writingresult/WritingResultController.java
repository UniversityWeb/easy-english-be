package com.universityweb.writingresult;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.section.service.SectionService;
import com.universityweb.writingresult.dto.WritingResultDTO;
import com.universityweb.writingresult.entity.WritingResult;
import com.universityweb.writingresult.req.WritingResultFilterReq;
import com.universityweb.writingresult.service.WritingResultService;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.service.WritingTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/writing-results")
@Tag(name = "Writing Results")
public class WritingResultController
        extends BaseController<WritingResult, WritingResultDTO, Long, WritingResultService> {

    private final AuthService authService;
    private final SectionService sectionService;
    private final WritingTaskService writingTaskService;

    @Autowired
    public WritingResultController(
            WritingResultService service,
            AuthService authService,
            SectionService sectionService,
            WritingTaskService writingTaskService
    ) {
        super(service);
        this.authService = authService;
        this.sectionService = sectionService;
        this.writingTaskService = writingTaskService;
    }

    @PostMapping("/get-results")
    public ResponseEntity<Page<WritingResultDTO>> getMyWritingResults(
            @RequestBody WritingResultFilterReq filterReq
    ) {
        String username = authService.getCurrentUsername();
        Long writingTaskId = filterReq.getWritingTaskId();
        WritingTask task = writingTaskService.getEntityById(writingTaskId);
        boolean isAccessible = sectionService.isAccessible(username, task.getSectionId());
        if (!isAccessible) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<WritingResultDTO> results = service.getByFilters(filterReq);
        return ResponseEntity.ok(results);
    }
}
