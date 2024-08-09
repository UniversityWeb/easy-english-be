package com.universityweb.common.auth.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.Token;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.ExpiredTokenException;
import com.universityweb.common.auth.exception.TokenNotFoundException;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.exception.UserNotFoundException;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.TokenRepos;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.LoginRequest;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.response.RegisterResponse;
import com.universityweb.common.auth.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper uMapper = UserMapper.INSTANCE;
    private final UserRepos userRepos;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepos tokenRepos;

    @Override
    public RegisterResponse registerStudentAccount(UserDTO userDTO) {
        String username = userDTO.getUsername();
        if (userRepos.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        String plainPassword = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        User user = uMapper.toEntity(userDTO);
        user.setPassword(encodedPassword);

        User saved = userRepos.save(user);
        UserDTO savedDTO = uMapper.toDTO(saved);
        return new RegisterResponse("Register successfully", savedDTO);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        Optional<User> userOpt = userRepos.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Could not found any user with username=" + username);
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        Token savedToken = addToken(user);
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
        boolean isExpired = JwtTokenUtil.isTokenExpired(tokenStr);
        if (isExpired) {
            throw new ExpiredTokenException("Expired token");
        }

        String username = JwtTokenUtil.extractUsername(tokenStr);
        User user = userRepos.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Could find any user with username=" + username));
        return uMapper.toDTO(user);
    }

    private Token addToken(User user) {
        LocalDateTime curTime = LocalDateTime.now();
        LocalDateTime expirationTime = curTime.plus(JwtTokenUtil.EXPIRATION_DURATION_MILLIS, ChronoUnit.MILLIS);
        String tokenStr = JwtTokenUtil.generateToken(user, expirationTime);
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
