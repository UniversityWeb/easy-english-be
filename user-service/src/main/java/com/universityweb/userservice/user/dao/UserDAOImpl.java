package com.universityweb.userservice.user.dao;

import com.universityweb.userservice.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsByUsername(String username) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String jpql = "SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("phoneNumber", phoneNumber);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    @Override
    public String getEmailByUsername(String username) {
        String jpql = "SELECT u.email FROM User u WHERE u.username = :username";
        return entityManager.createQuery(jpql, String.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public String getUsernameByEmail(String email) {
        String jpql = "SELECT u.username FROM User u WHERE u.email = :email";
        return entityManager.createQuery(jpql, String.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Override
    public Page<User> findAllNonRoleUsersWithFilters(
            List<User.ERole> excludedRoles,
            User.EStatus status,
            String fullName,
            String email,
            String phoneNumber,
            User.EGender gender,
            User.ERole role,
            Pageable pageable) {

        StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE 1=1");

        if (excludedRoles != null && !excludedRoles.isEmpty()) {
            jpql.append(" AND u.role NOT IN :excludedRoles");
        }
        if (status != null) {
            jpql.append(" AND u.status = :status");
        }
        if (fullName != null && !fullName.isEmpty()) {
            jpql.append(" AND LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))");
        }
        if (email != null && !email.isEmpty()) {
            jpql.append(" AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))");
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            jpql.append(" AND LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))");
        }
        if (gender != null) {
            jpql.append(" AND u.gender = :gender");
        }
        if (role != null) {
            jpql.append(" AND u.role = :role");
        }

        TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class);

        if (excludedRoles != null && !excludedRoles.isEmpty()) {
            query.setParameter("excludedRoles", excludedRoles);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (fullName != null && !fullName.isEmpty()) {
            query.setParameter("fullName", fullName);
        }
        if (email != null && !email.isEmpty()) {
            query.setParameter("email", email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            query.setParameter("phoneNumber", phoneNumber);
        }
        if (gender != null) {
            query.setParameter("gender", gender);
        }
        if (role != null) {
            query.setParameter("role", role);
        }

        // Pagination
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<User> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, totalRows);
    }

    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}