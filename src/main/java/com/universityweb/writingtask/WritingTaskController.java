package com.universityweb.writingtask;

import com.fasterxml.jackson.databind.JsonNode;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.section.service.SectionService;
import com.universityweb.writingresult.entity.WritingResult;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.req.WritingTaskFilterReq;
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
@RequestMapping("/api/v1/writing-tasks")
@Tag(name = "Writing Tasks")
public class WritingTaskController
        extends BaseController<WritingTask, WritingTaskDTO, Long, WritingTaskService> {

    private final AuthService authService;
    private final SectionService sectionService;

    @Autowired
    public WritingTaskController(
            WritingTaskService service,
            AuthService authService,
            SectionService sectionService
    ) {
        super(service);
        this.authService = authService;
        this.sectionService = sectionService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<WritingTaskDTO> create(WritingTaskDTO dto) {
        return super.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<WritingTaskDTO> update(Long id, WritingTaskDTO dto) {
        return super.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Override
    public ResponseEntity<Void> delete(Long id) {
        return super.delete(id);
    }

    @PostMapping("/get-tasks")
    public ResponseEntity<Page<WritingTaskDTO>> getMyWritingTasks(
            @RequestBody WritingTaskFilterReq filterReq
    ) {
        String username = authService.getCurrentUsername();
        boolean isAccessible = sectionService.isAccessible(username, filterReq.getSectionId());
        if (!isAccessible) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<WritingTaskDTO> tasks = service.getByFilters(filterReq);
        return ResponseEntity.ok(tasks);
    }


}
