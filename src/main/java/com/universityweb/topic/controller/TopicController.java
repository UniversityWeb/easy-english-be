package com.universityweb.topic.controller;

import com.universityweb.topic.request.TopicRequest;
import com.universityweb.topic.service.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/api/v1/topic")
@RestController
@Tag(name = "Topics")
public class TopicController {

    @Autowired
    private TopicService topicService;
    @PostMapping("create-topic")
    public ResponseEntity<String> createTopic(@RequestBody TopicRequest topicRequest) {
        topicService.createTopic(topicRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Topic added successfully");
    }

    @PostMapping("update-topic")
    public ResponseEntity<String> updateTopic(@RequestBody TopicRequest topicRequest) {
        topicService.updateTopic(topicRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Topic updated successfully");
    }

    @PostMapping("get-all-topic")
    public ResponseEntity<?> getAllTopic() {
        return ResponseEntity.ok(topicService.getAllTopic());
    }
}
