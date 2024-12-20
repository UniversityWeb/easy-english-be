package com.universityweb.common.uc;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.request.RegisterRequest;
import com.universityweb.common.auth.service.auth.AuthServiceImpl;
import com.universityweb.common.auth.service.otp.OtpService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_001_Register_Tests {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserMapper uMapper;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterStudentAccount_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "john", "Pp@123456", "John Doe", "john@gmail.com",
                "+84972640891", User.EGender.MALE, LocalDate.of(2000, 1, 1), null
        );

        String encodedPassword = passwordEncoder.encode("Pp@123456");
        User user = User.builder()
                .username(request.username())
                .password(encodedPassword)
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .bio("")
                .gender(request.gender())
                .dob(request.dob())
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .avatarPath(request.avatarPath())
                .status(User.EStatus.INACTIVE)
                .build();

        UserDTO userDTO = new UserDTO(
                request.username(),
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                "",  // bio
                request.gender(),
                request.dob(),
                User.ERole.STUDENT,
                LocalDateTime.now(),
                null // avatarPath
        );

        when(userService.existsByUsername(request.username())).thenReturn(false);
        when(userService.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(userService.save(any(User.class))).thenReturn(user);
        when(uMapper.toDTO(user)).thenReturn(userDTO);

        // Act
        UserDTO result = authService.registerStudentAccount(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.username(), result.getUsername());
        assertEquals(request.email(), result.getEmail());
        verify(otpService, times(1)).generateAndSendOtp(request.email(), OtpService.EPurpose.ACTIVE_ACCOUNT);
    }

    @Test
    void testRegisterStudentAccount_UsernameAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "john", "P@123456789", "John Doe", "john@gmail.com",
                "+84972640891", User.EGender.MALE, LocalDate.of(2000, 1, 1), null
        );

        when(userService.existsByUsername(request.username())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.registerStudentAccount(request)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userService, never()).save(any(User.class));
        verify(otpService, never()).generateAndSendOtp(anyString(), any(OtpService.EPurpose.class));
    }

    @Test
    void testRegisterStudentAccount_EmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "john", "P@123456789", "John Doe", "john@gmail.com",
                "+84972640891", User.EGender.MALE, LocalDate.of(2000, 1, 1), null
        );

        when(userService.existsByUsername(request.username())).thenReturn(false);
        when(userService.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authService.registerStudentAccount(request)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userService, never()).save(any(User.class));
        verify(otpService, never()).generateAndSendOtp(anyString(), any(OtpService.EPurpose.class));
    }
}
