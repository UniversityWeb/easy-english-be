package com.universityweb.notification;

import com.universityweb.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepos extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserUsername(String username, Pageable pageable);
}
