package com.universityweb.topic.service;

import com.universityweb.topic.entity.Topic;
import com.universityweb.topic.mapper.TopicMapper;
import com.universityweb.topic.request.TopicRequest;
import com.universityweb.topic.response.TopicResponse;
import com.universityweb.topic.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService {
    private TopicMapper topicMapper = TopicMapper.INSTANCE;
    @Autowired
    private TopicRepository topicRepository;

    public void createTopic(TopicRequest topicRequest) {
        Topic topic = new Topic();
        topic.setName(topicRequest.getName());
        topicRepository.save(topic);
    }
    public void updateTopic(TopicRequest topicRequest) {
        Topic currentTopic = topicRepository
                .findById(topicRequest.getId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        currentTopic.setName(topicRequest.getName());
        topicRepository.save(currentTopic);
    }

    public List<TopicResponse> getAllTopic() {
        List<Topic> topics = topicRepository.findAll();
        List<TopicResponse> topicResponses = new ArrayList<>();
        topics.forEach(topic -> {
            TopicResponse topicResponse = new TopicResponse();
            topicResponse.setId(topic.getId());
            topicResponse.setName(topic.getName());
            topicResponses.add(topicResponse);
        });
        return topicResponses;
    }

}
