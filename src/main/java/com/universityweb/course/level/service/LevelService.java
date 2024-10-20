package com.universityweb.course.level.service;

import com.universityweb.course.level.entity.Level;
import com.universityweb.course.topic.entity.Topic;
import com.universityweb.course.level.request.LevelRequest;
import com.universityweb.course.level.response.LevelResponse;
import com.universityweb.course.level.LevelRepository;
import com.universityweb.course.topic.TopicRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LevelService {
    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private TopicRepository topicRepository;
    public void createLevel(LevelRequest levelRequest) {
        Level level = new Level();
        BeanUtils.copyProperties(levelRequest, level);
        Topic topic = topicRepository.findById(levelRequest.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        level.setTopic(topic);
        levelRepository.save(level);
    }

    public void updateLevel(LevelRequest levelRequest) {
        Level currentLevel = levelRepository.findById(levelRequest.getId())
                .orElseThrow(() -> new RuntimeException("Level not found"));
        BeanUtils.copyProperties(levelRequest, currentLevel, "id");
        levelRepository.save(currentLevel);
    }

    public List<LevelResponse> getLevelByTopic(LevelRequest levelRequest) {
        Long topicId = levelRequest.getTopicId();
        List<Level> levels = levelRepository.findByTopicId(topicId);
        List<LevelResponse> levelResponses = new ArrayList<>();
        levels.forEach(level -> {
            LevelResponse levelResponse = new LevelResponse();
            BeanUtils.copyProperties(level, levelResponse);
            levelResponses.add(levelResponse);
        });
        return levelResponses;
    }
}
