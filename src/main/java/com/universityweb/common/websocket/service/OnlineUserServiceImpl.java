package com.universityweb.common.websocket.service;

import com.universityweb.common.websocket.CustomMessageHandler;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final Logger log = LogManager.getLogger(OnlineUserServiceImpl.class);

    // Timeout duration to consider a user offline (60 seconds + a small buffer of 5s = 65000ms)
    private static final long OFFLINE_TIMEOUT_MS = 65000;

    private final Map<String, Long> activeUsers = new ConcurrentHashMap<>();
    private final CustomMessageHandler messageHandler;

    /**
     * Updates the last active time of a user.
     */
    @Override
    public void ping(String username) {
        long now = System.currentTimeMillis();
        boolean wasOffline = !activeUsers.containsKey(username);
        
        activeUsers.put(username, now);
        
        if (wasOffline) {
            log.info("User {} is now online via ping.", username);
            broadcastOnlineUsers();
        }
    }

    @Override
    public void cleanupOfflineUsers() {
        long threshold = System.currentTimeMillis() - OFFLINE_TIMEOUT_MS;
        boolean changed = false;

        List<String> offlineUsers = new ArrayList<>();

        for (Map.Entry<String, Long> entry : activeUsers.entrySet()) {
            if (entry.getValue() < threshold) {
                offlineUsers.add(entry.getKey());
            }
        }

        for (String username : offlineUsers) {
            activeUsers.remove(username);
            log.info("User {} went offline due to timeout.", username);
            changed = true;
        }

        if (changed) {
            broadcastOnlineUsers();
        }
    }

    @Override
    public List<String> getOnlineUsers() {
        return new ArrayList<>(activeUsers.keySet());
    }

    private void broadcastOnlineUsers() {
        messageHandler.sendOnlineUsers(getOnlineUsers());
    }
}
