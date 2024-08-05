package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepos extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
}
