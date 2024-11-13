package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepos extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.email FROM User u WHERE u.username = :username")
    String getEmailByUsername(@Param("username") String username);

    @Query(value = "SELECT u.username FROM User u WHERE u.email = :email")
    String getUsernameByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role NOT IN :excludedRoles " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:startDate IS NULL OR u.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR u.createdAt <= :endDate) " +
            "AND (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<User> findAllNonAdminUsersWithFilters(
            @Param("excludedRoles") List<User.ERole> excludedRoles,
            @Param("status") User.EStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("fullName") String fullName,
            @Param("email") String email,
            Pageable pageable
    );
}
