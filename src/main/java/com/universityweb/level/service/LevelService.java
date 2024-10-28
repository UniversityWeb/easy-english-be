package com.universityweb.level.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.level.entity.Level;
import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.response.LevelResponse;

import java.util.List;

public interface LevelService extends BaseService<Level, LevelResponse, Long> {
    void createLevel(LevelRequest levelRequest);

    void updateLevel(LevelRequest levelRequest);

    List<LevelResponse> getLevelByTopic(LevelRequest levelRequest);
}
