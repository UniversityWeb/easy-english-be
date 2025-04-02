package com.universityweb.writingtask;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.service.WritingTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/writing-tasks")
@Tag(name = "Writing Tasks")
public class WritingTaskController
        extends BaseController<WritingTask, WritingTaskDTO, Long, WritingTaskService> {

    @Autowired
    public WritingTaskController(
            WritingTaskService service
    ) {
        super(service);
    }
}
