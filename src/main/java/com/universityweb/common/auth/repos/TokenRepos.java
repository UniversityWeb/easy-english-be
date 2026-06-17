package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.Token;
import com.universityweb.common.auth.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepos extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {
    @Modifying
    @Transactional
    void deleteByUser(User user);

    List<Token> findByUser_Username(String username);
    Optional<Token> findByTokenStr(String token);

    @EntityGraph(attributePaths = {"user"})
    Optional<Token> findWithUserByTokenStr(String token);

    @EntityGraph(attributePaths = {"user"})
    Optional<Token> findWithUserByRefreshTokenStr(String refreshTokenStr);

    Optional<Token> findByRefreshTokenStr(String refreshTokenStr);

    @Modifying
    @Transactional
    void deleteByTokenStr(String tokenStr);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.user.username = :username")
    void deleteByUserUsername(String username);

    Optional<Token> findByUser_UsernameAndDeviceInfo(String username, String deviceInfo);
}
