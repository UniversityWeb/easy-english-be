package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepos extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);

    @Query(value = """
        SELECT u.email FROM User u WHERE u.username = :username
    """)
    String getEmailByUsername(@Param("username") String username);

    @Query(value = """
        SELECT u.username FROM User u WHERE u.email = :email
    """)
    String getUsernameByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u
        FROM User u
        WHERE
            (:excludedRoles IS NULL OR u.role NOT IN :excludedRoles)
            AND (:status IS NULL OR u.status = :status)
            AND (:dob IS NULL OR u.dob = :dob)
            AND (:createdAt IS NULL OR u.createdAt = :createdAt)
            AND (
                :fullName IS NULL OR :fullName = '' 
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))
            )
            AND (
                :email IS NULL OR :email = '' 
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))
            )
    """)
    Page<User> findAllNonRoleUsersWithFilters(
            @Param("excludedRoles") List<User.ERole> excludedRoles,
            @Param("status") User.EStatus status,
            @Param("dob") LocalDate dob,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("fullName") String fullName,
            @Param("email") String email,
            Pageable pageable
    );
}
