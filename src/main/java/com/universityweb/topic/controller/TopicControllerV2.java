package com.universityweb.topic.controller;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.topic.entity.Topic;
import com.universityweb.topic.response.TopicResponse;
import com.universityweb.topic.service.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("/api/v2/topic")
@RestController
@Tag(name = "Topics v2")
public class TopicControllerV2
        extends BaseController<Topic, TopicResponse, Long, TopicService> {

    @Autowired
    public TopicControllerV2(TopicService service) {
        super(service);
    }
}
