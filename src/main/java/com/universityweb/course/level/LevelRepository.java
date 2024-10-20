package com.universityweb.course.level;

import com.universityweb.course.level.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findById(Long id);

    List<Level> findByTopicId(Long topicId);
}
