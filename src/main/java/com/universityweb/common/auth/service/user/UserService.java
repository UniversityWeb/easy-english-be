package com.universityweb.common.auth.service.user;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.request.GetUserFilterReq;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import com.universityweb.common.infrastructure.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService, BaseService<User, UserDTO, String> {
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    UserDTO getUserByUsername(String username) throws UsernameNotFoundException;

    boolean existsByUsername(String username);

    User save(User user);

    List<User> saveAll(List<User> users);

    UserDTO update(UpdateProfileRequest updateProfileRequest);

    String getEmailByUsername(String username);

    User getUserByEmail(String email);

    Page<UserDTO> getUsersWithoutAdmin(GetUserFilterReq filterReq);
}
