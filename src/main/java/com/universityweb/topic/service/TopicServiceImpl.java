package com.universityweb.topic.service;

import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.topic.TopicRepository;
import com.universityweb.topic.entity.Topic;
import com.universityweb.topic.mapper.TopicMapper;
import com.universityweb.topic.request.TopicRequest;
import com.universityweb.topic.response.TopicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicServiceImpl
        extends BaseServiceImpl<Topic, TopicResponse, Long, TopicRepository, TopicMapper>
        implements TopicService{

    @Autowired
    public TopicServiceImpl(
            TopicRepository repository,
            TopicMapper mapper
    ) {
        super(repository, mapper);
    }

    public void createTopic(TopicRequest topicRequest) {
        Topic topic = new Topic();
        topic.setName(topicRequest.getName());
        repository.save(topic);
    }
    public void updateTopic(TopicRequest topicRequest) {
        Topic currentTopic = getEntityById(topicRequest.getId());
        currentTopic.setName(topicRequest.getName());
        repository.save(currentTopic);
    }

    public List<TopicResponse> getAllTopic() {
        List<Topic> topics = repository.findAll();
        List<TopicResponse> topicResponses = new ArrayList<>();
        topics.forEach(topic -> {
            TopicResponse topicResponse = new TopicResponse();
            topicResponse.setId(topic.getId());
            topicResponse.setName(topic.getName());
            topicResponses.add(topicResponse);
        });
        return topicResponses;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Topic not found");
    }

    @Override
    public TopicResponse update(Long id, TopicResponse dto) {
        Topic currentTopic = getEntityById(id);

        if (!currentTopic.getCourses().isEmpty()) {
            throw new IllegalStateException("Topic update failed: associated with courses");
        }

        currentTopic.setName(dto.getName());
        return savedAndConvertToDTO(currentTopic);
    }

    @Override
    public void softDelete(Long id) {
        Topic topic = getEntityById(id);

        if (!topic.getCourses().isEmpty()) {
            throw new IllegalStateException("Topic delete failed: associated with courses");
        }

        topic.setIsDeleted(true);
        repository.save(topic);
    }
}
