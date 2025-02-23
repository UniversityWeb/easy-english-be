package com.universityweb.userservice.user.dao;

import com.universityweb.userservice.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    boolean existsByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    String getEmailByUsername(String username);
    String getUsernameByEmail(String email);
    Optional<User> findByEmail(String email);
    Page<User> findAllNonRoleUsersWithFilters(
            List<User.ERole> excludedRoles,
            User.EStatus status,
            String fullName,
            String email,
            String phoneNumber,
            User.EGender gender,
            User.ERole role,
            Pageable pageable
    );
    boolean existsByEmail(String email);
}
