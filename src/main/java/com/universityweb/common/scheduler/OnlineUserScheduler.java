package com.universityweb.common.scheduler;

import com.universityweb.common.websocket.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineUserScheduler {

    private final OnlineUserService onlineUserService;

    /**
     * Filters and removes users who haven't sent a ping within the timeout duration.
     * The fixedRate is configured in application.yml.
     */
    @Scheduled(fixedRateString = "${app.scheduler.online-user-cleanup-rate:60000}")
    public void runCleanupJob() {
        onlineUserService.cleanupOfflineUsers();
    }
}
