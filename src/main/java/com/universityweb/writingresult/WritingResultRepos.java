package com.universityweb.writingresult;

import com.universityweb.writingresult.entity.WritingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingResultRepos extends JpaRepository<WritingResult, Long> {
}
