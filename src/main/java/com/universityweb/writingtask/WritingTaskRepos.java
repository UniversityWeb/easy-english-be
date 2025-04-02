package com.universityweb.writingtask;

import com.universityweb.writingtask.entity.WritingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingTaskRepos extends JpaRepository<WritingTask, Long> {
}
