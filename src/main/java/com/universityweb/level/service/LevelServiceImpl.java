package com.universityweb.level.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.level.LevelRepository;
import com.universityweb.level.entity.Level;
import com.universityweb.level.mapper.LevelMapper;
import com.universityweb.level.request.LevelRequest;
import com.universityweb.level.response.LevelResponse;
import com.universityweb.topic.TopicRepository;
import com.universityweb.topic.entity.Topic;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LevelServiceImpl
        extends BaseServiceImpl<Level, LevelResponse, Long, LevelRepository, LevelMapper>
        implements LevelService {

    private final TopicRepository topicRepository;

    @Autowired
    public LevelServiceImpl(LevelRepository repository, TopicRepository topicRepository) {
        super(repository, LevelMapper.INSTANCE);
        this.topicRepository = topicRepository;
    }

    @Override
    public void createLevel(LevelRequest levelRequest) {
        Level level = mapper.toEntity(levelRequest);
        Topic topic = topicRepository.findById(levelRequest.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        level.setTopic(topic);
        repository.save(level);
    }

    @Override
    public void updateLevel(LevelRequest levelRequest) {
        Level currentLevel = getEntityById(levelRequest.getId());
        BeanUtils.copyProperties(levelRequest, currentLevel, "id");
        repository.save(currentLevel);
    }

    @Override
    public List<LevelResponse> getLevelByTopic(LevelRequest levelRequest) {
        Long topicId = levelRequest.getTopicId();
        List<Level> levels = repository.findByTopicId(topicId);
        List<LevelResponse> levelResponses = new ArrayList<>();
        levels.forEach(level -> {
            LevelResponse levelResponse = mapper.toDTO(level);
            levelResponses.add(levelResponse);
        });
        return levelResponses;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Level not found");
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Level entity, LevelResponse dto) {
        Topic topic = topicRepository.findById(dto.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        entity.setTopic(topic);
    }

    @Override
    public LevelResponse update(Long id, LevelResponse dto) {
        Level currentLevel = getEntityById(id);
        BeanUtils.copyProperties(dto, currentLevel, "id");
        Level saved = repository.save(currentLevel);
        return mapper.toDTO(saved);
    }

    @Override
    public void softDelete(Long id) {
        Level level = getEntityById(id);
        level.setIsDeleted(true);
        repository.save(level);
    }
}
