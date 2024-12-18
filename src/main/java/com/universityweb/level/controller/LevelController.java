package com.universityweb.level.controller;

import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.service.LevelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/api/v1/level")
@RestController
@Tag(name = "Levels")
public class LevelController {
    @Autowired
    private LevelService levelService;
    @PostMapping("create-level")
    public ResponseEntity<String> createLevel(@RequestBody LevelRequest levelRequest) {
        levelService.createLevel(levelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Level added successfully");
    }

    @PostMapping("update-level")
    public ResponseEntity<String> updateLevel(@RequestBody LevelRequest levelRequest) {
        levelService.updateLevel(levelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Level updated successfully");
    }

    @PostMapping("get-all-level-by-topic")
    public ResponseEntity<?> getLevelByTopic(@RequestBody LevelRequest levelRequest) {
        return ResponseEntity.ok(levelService.getLevelByTopic(levelRequest));
    }
}
