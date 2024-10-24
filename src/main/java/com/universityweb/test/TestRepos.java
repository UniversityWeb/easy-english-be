package com.universityweb.test;

import com.universityweb.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepos extends JpaRepository<Test, Long> {
}
