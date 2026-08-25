package com.universityweb.common.websocket.service;

import java.util.List;

public interface OnlineUserService {
    
    /**
     * Updates the last active time of a user.
     * @param username The username of the pinging user.
     */
    void ping(String username);

    /**
     * Removes users who have exceeded the inactivity timeout.
     */
    void cleanupOfflineUsers();

    /**
     * Gets a list of all currently online users.
     * @return List of online usernames.
     */
    List<String> getOnlineUsers();
}
