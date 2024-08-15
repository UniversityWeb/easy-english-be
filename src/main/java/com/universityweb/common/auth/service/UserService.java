package com.universityweb.common.auth.service;

import com.universityweb.common.auth.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    boolean existsByUsername(String username);

    User save(User user);
}
