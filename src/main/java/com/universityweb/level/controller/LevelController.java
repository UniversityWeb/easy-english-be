package com.universityweb.level.controller;

import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.service.LevelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/levels")
@Tag(name = "Levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    public LevelController(LevelService levelService) {
        this.levelService = levelService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createLevel(@RequestBody LevelRequest levelRequest) {
        levelService.createLevel(levelRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Level created successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateLevel(@RequestBody LevelRequest levelRequest) {
        levelService.updateLevel(levelRequest);
        return ResponseEntity
                .ok("Level updated successfully");
    }

    @PostMapping("/by-topic")
    public ResponseEntity<?> getLevelsByTopic(@RequestBody LevelRequest levelRequest) {
        return ResponseEntity.ok(
                levelService.getLevelByTopic(levelRequest)
        );
    }
}
