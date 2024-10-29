package com.universityweb.level.controller;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.level.entity.Level;
import com.universityweb.level.response.LevelResponse;
import com.universityweb.level.service.LevelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/api/v2/level")
@RestController
@Tag(name = "Levels v2")
public class LevelControllerV2
        extends BaseController<Level, LevelResponse, Long, LevelService> {

    @Autowired
    public LevelControllerV2(LevelService service) {
        super(service);
    }
}
