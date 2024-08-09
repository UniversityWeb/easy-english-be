package com.universityweb.common.auth.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.request.LoginRequest;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.response.RegisterResponse;

public interface AuthService {

    RegisterResponse registerStudentAccount(UserDTO userDTO);

    LoginResponse login(LoginRequest loginRequest);

    void logout(String tokenStr);

    UserDTO getUserByTokenStr(String tokenStr);
}
