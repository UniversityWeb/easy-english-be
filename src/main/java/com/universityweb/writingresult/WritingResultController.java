package com.universityweb.writingresult;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.universityweb.common.auth.mapper.UserMapper.objectMapper;

@RestController
@RequestMapping("/api/v1/writing-results")
@Tag(name = "Writing Results")
public class WritingResultController
        extends BaseController<WritingResult, WritingResultDTO, Long, WritingResultService> {

    @Value("${gemini.api-key}")
    private String GEMINI_API_KEY;

    @Value("${gemini.url}")
    private String GEMINI_URL;

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

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/submit")
    @Override
    public ResponseEntity<WritingResultDTO> create(WritingResultDTO dto) {
        return super.create(dto);
    }

    @Override
    public void preCreate(WritingResultDTO dto) {
        String curUsername = authService.getCurrentUsername();
        dto.setOwnerUsername(curUsername);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<WritingResultDTO> update(Long id, WritingResultDTO dto) {
        return super.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
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
