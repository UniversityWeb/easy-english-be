package com.universityweb.questiongroup;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.service.QuestionGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/question-groups")
@Tag(name = "Question Groups")
public class QuestionGroupServiceController
        extends BaseController<QuestionGroup, QuestionGroupDTO, Long, QuestionGroupService> {

    @Autowired
    public QuestionGroupServiceController(QuestionGroupService service) {
        super(service);
    }
}
