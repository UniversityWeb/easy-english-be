package com.universityweb.topic.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.topic.entity.Topic;
import com.universityweb.topic.request.TopicRequest;
import com.universityweb.topic.response.TopicResponse;

import java.util.List;

public interface TopicService extends BaseService<Topic, TopicResponse, Long> {
    void createTopic(TopicRequest topicRequest);
    void updateTopic(TopicRequest topicRequest);
    List<TopicResponse> getAllTopic();
}
