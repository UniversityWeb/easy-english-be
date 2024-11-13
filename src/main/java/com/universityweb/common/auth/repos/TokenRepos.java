package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.Token;
import com.universityweb.common.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepos extends JpaRepository<Token, Long> {
    @Modifying
    @Transactional
    void deleteByUser(User user);
    Optional<Token> findByUser_Username(String username);
    Optional<Token> findByTokenStr(String token);
}
