package com.universityweb.common.auth.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.repos.UserRepos;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepos userRepos;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        String msg = String.format("Could not find any user with username=%s", username);
        User user = userRepos.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(msg));
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        boolean isExists = userRepos.existsByUsername(username);
        if (isExists) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        return false;
    }

    @Override
    public User save(User user) {
        return userRepos.save(user);
    }
}
