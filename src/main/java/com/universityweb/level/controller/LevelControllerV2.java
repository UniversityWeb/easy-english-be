package com.universityweb.level.controller;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.level.entity.Level;
import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.response.LevelResponse;
import com.universityweb.level.service.LevelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/levels")
@RestController
@Tag(name = "Levels v2")
public class LevelControllerV2
        extends BaseController<Level, LevelResponse, Long, LevelService> {

    @Autowired
    public LevelControllerV2(LevelService service) {
        super(service);
    }

    @PostMapping("/get-all-level-by-topic")
    public ResponseEntity<List<LevelResponse>> getLevelByTopic(@RequestBody LevelRequest levelRequest) {
        return ResponseEntity.ok(service.getLevelByTopic(levelRequest));
    }
}
