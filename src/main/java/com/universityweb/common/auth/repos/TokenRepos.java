package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepos extends JpaRepository<Token, Long> {

    @Query(value = "SELECT t FROM Token t WHERE t.user.username = :username")
    List<Token> findByUsername(@Param("username") String username);

    Optional<Token> findByTokenStr(String tokenStr);
}
