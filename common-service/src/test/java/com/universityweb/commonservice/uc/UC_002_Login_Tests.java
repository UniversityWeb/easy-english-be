package com.universityweb.commonservice.uc;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.LoginRequest;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.service.auth.AuthServiceImpl;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.security.JwtGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_002_Login_Tests {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepos userRepos;

    @Mock
    private JwtGenerator jwtGenerator;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserMapper uMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    void testLogin_Success_AUTH_LOGIN_POS_001() {
        // Arrange
        LoginRequest request = new LoginRequest("", "john@gmail.com", "P@123456789");

        User user = User.builder()
                .username("john")
                .email("john@gmail.com")
                .password("encodedPassword")
                .status(User.EStatus.ACTIVE)
                .build();

        when(userRepos.getUsernameByEmail(request.usernameOrEmail())).thenReturn("john");
        when(userService.loadUserByUsername("john")).thenReturn(user);
        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtGenerator.generateAndSaveToken(user)).thenReturn("mockJwtToken");
        when(uMapper.toDTO(user)).thenReturn(new UserDTO("john", "John Doe",
                "john@gmail.com", "+84972640891", "", User.EGender.MALE,
                LocalDate.of(2000, 1, 1), User.ERole.STUDENT, LocalDateTime.now(), null));

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("Login successfully", response.getMessage());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("mockJwtToken", response.getTokenStr());
        assertEquals(User.EStatus.ACTIVE, response.getAccountStatus());
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @Transactional
    void testLogin_InvalidPassword_AUTH_LOGIN_NEG_001() {
        // Arrange
        LoginRequest request = new LoginRequest("", "john", "wrongPassword");

        User user = User.builder()
                .username("john")
                .email("john@gmail.com")
                .password("encodedPassword")
                .status(User.EStatus.ACTIVE)
                .build();

        when(userService.loadUserByUsername("john")).thenReturn(user);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new CustomException("Invalid credentials"));

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
