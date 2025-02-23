package com.universityweb.engagementservice.useranswer;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import com.universityweb.useranswer.entity.UserAnswer;
import com.universityweb.useranswer.service.UserAnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-answers")
@Tag(name = "User Answers")
public class UserAnswerController
        extends BaseController<UserAnswer, UserAnswerDTO, Long, UserAnswerService> {

    @Autowired
    public UserAnswerController(UserAnswerService service) {
        super(service);
    }
}
