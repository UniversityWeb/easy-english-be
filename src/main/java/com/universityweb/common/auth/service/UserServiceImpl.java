package com.universityweb.common.auth.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper uMapper = UserMapper.INSTANCE;

    @Autowired
    private UserRepos userRepos;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        String msg = String.format("Could not find any user with username=%s", username);
        User user = userRepos.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(msg));
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepos.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepos.save(user);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return userRepos.saveAll(users);
    }

    @Override
    public UserDTO update(UpdateProfileRequest updateProfileRequest) {
        String usernameToUpdate = updateProfileRequest.username();

        User user = loadUserByUsername(usernameToUpdate);

        user.setFullName(usernameToUpdate);
        user.setEmail(updateProfileRequest.email());
        user.setPhoneNumber(updateProfileRequest.phoneNumber());
        user.setBio(updateProfileRequest.bio());
        user.setGender(updateProfileRequest.gender());
        user.setDob(updateProfileRequest.dob());

        User savedUser = userRepos.save(user);
        return uMapper.toDTO(savedUser);
    }
}
