package com.universityweb.common.auth.service.user;

import com.universityweb.userservice.user.dto.UserDTO;
import com.universityweb.userservice.user.dto.UserForAdminDTO;
import com.universityweb.userservice.user.entity.User;
import com.universityweb.userservice.user.req.GetUserFilterReq;
import com.universityweb.userservice.user.req.UpdateProfileRequest;
import org.springframework.data.domain.Page;

public interface UserService extends BaseService<User, UserDTO, String> {
    User loadUserByUsername(String username);

    boolean existsByUsername(String username);

    UserDTO update(UpdateProfileRequest updateProfileRequest);

    String getEmailByUsername(String username);

    User getUserByEmail(String email);

    Page<UserForAdminDTO> getUsersWithoutAdmin(GetUserFilterReq filterReq);

    UserForAdminDTO updateUserForAdmin(String username, UserForAdminDTO req);

    UserForAdminDTO addUserForAdmin(UserForAdminDTO req);

    boolean existsByEmail(String email);
}
