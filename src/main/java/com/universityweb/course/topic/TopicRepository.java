package com.universityweb.course.topic;

import com.universityweb.course.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findById(Long id);
}
