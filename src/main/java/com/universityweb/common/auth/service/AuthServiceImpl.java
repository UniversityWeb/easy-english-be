package com.universityweb.common.auth.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.Token;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.TokenNotFoundException;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.TokenRepos;
import com.universityweb.common.auth.request.LoginRequest;
import com.universityweb.common.auth.request.RegisterRequest;
import com.universityweb.common.auth.request.UpdatePasswordRequest;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.response.RegisterResponse;
import com.universityweb.common.security.JwtGenerator;
import com.universityweb.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper uMapper = UserMapper.INSTANCE;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final TokenRepos tokenRepos;
    private final AuthenticationManager authManager;

    @Override
    public RegisterResponse registerStudentAccount(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        boolean isExists = userService.existsByUsername(username);
        if (isExists) {
            String msg = "Username already exists";
            throw new UserAlreadyExistsException(msg);
        }

        String plainPassword = registerRequest.password();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .fullName(registerRequest.fullName())
                .email(registerRequest.email())
                .phoneNumber(registerRequest.phoneNumber())
                .bio("")
                .gender(registerRequest.gender())
                .dob(registerRequest.dob())
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        user.setPassword(encodedPassword);

        User saved = userService.save(user);
        UserDTO savedDTO = uMapper.toDTO(saved);
        return new RegisterResponse("Register successfully", savedDTO);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Token savedToken = addToken(authentication);
        return new LoginResponse("Login successfully", "Bearer", savedToken.getTokenStr());
    }

    @Override
    public void logout(String tokenStr) {
        Optional<Token> tokenOpt = tokenRepos.findByTokenStr(tokenStr);
        if (tokenOpt.isEmpty()) {
            throw new TokenNotFoundException("Could not find any tokenStr with tokenStr=" + tokenStr);
        }

        Token token = tokenOpt.get();
        token.setDeleted(true);
        tokenRepos.save(token);
    }

    @Override
    public UserDTO getUserByTokenStr(String tokenStr) {
        jwtGenerator.validateToken(tokenStr);

        String username = jwtGenerator.getUsernameFromJwt(tokenStr);
        User user = userService.loadUserByUsername(username);
        return uMapper.toDTO(user);
    }

    @Override
    public String getCurrentUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public void checkAuthorization(String targetUsername) {
        String currentUsername = getCurrentUsername();
        if (!targetUsername.equals(currentUsername)) {
            String msg = "User not authorized to access or modify this information";
            throw new SecurityException(msg);
        }
    }

    @Override
    public UserDTO updateOwnPassword(UpdatePasswordRequest request) {
        String usernameToUpdate = request.username();
        checkAuthorization(usernameToUpdate);

        if (!request.password().equals(request.confirmPassword())) {
            throw new SecurityException("Passwords do not match");
        }

        User user = userService.loadUserByUsername(usernameToUpdate);
        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);

        User savedUser = userService.save(user);
        return uMapper.toDTO(savedUser);
    }

    private Token addToken(Authentication authentication) {
        LocalDateTime curTime = LocalDateTime.now();
        LocalDateTime expirationTime = curTime.plus(SecurityUtils.EXPIRATION_DURATION_MILLIS, ChronoUnit.MILLIS);
        String tokenStr = jwtGenerator.generateToken(authentication, curTime, expirationTime);
        User user = userService.loadUserByUsername(authentication.getName());
        Token token = Token.builder()
                .tokenStr(tokenStr)
                .createdAt(curTime)
                .expiresAt(expirationTime)
                .isDeleted(false)
                .user(user)
                .build();
        return tokenRepos.save(token);
    }
}
