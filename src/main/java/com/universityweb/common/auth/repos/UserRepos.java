package com.universityweb.common.auth.repos;

import com.universityweb.common.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
            AND (
                :fullName IS NULL OR :fullName = '' 
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))
            )
            AND (
                :email IS NULL OR :email = '' 
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))
            )
            AND (
                :phoneNumber IS NULL OR :phoneNumber = '' 
                OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))
            )
            AND (
                :gender IS NULL OR u.gender = :gender
            )
            AND (
                :role IS NULL OR u.role = :role
            )
    """)
    Page<User> findAllNonRoleUsersWithFilters(
            @Param("excludedRoles") List<User.ERole> excludedRoles,
            @Param("status") User.EStatus status,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("gender") User.EGender gender,
            @Param("role") User.ERole role,
            Pageable pageable
    );

    boolean existsByEmail(String email);
}
