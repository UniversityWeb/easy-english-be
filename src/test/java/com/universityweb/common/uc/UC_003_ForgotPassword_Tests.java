package com.universityweb.common.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.ResetPassWithOtpReq;
import com.universityweb.common.auth.service.auth.AuthServiceImpl;
import com.universityweb.common.auth.service.otp.OtpService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_003_ForgotPassword_Tests {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserService userService;

    @Mock
    private OtpService otpService;

    @Mock
    private UserRepos userRepos;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testForgotPassword_Success_AUTH_FORGOT_PASS_POS_001() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            // Arrange - Generate OTP
            String email = "newusername@gmail.com";
            mockedAuthUtils.when(() -> AuthUtils.isValidEmail(email)).thenReturn(true);

            // Mock getUserByEmail to return a dummy user
            User user = new User();
            user.setEmail(email);

            when(userService.getUserByEmail(email)).thenReturn(user);
            doNothing().when(otpService).generateAndSendOtp(email, OtpService.EPurpose.RESET_PASS);

            // Act - Generate OTP
            authService.generateOtpToResetPassword(email);

            // Assert - Generate OTP
            verify(userService, times(1)).getUserByEmail(email);
            verify(otpService, times(1)).generateAndSendOtp(email, OtpService.EPurpose.RESET_PASS);

            // Arrange - Reset Password
            ResetPassWithOtpReq req = new ResetPassWithOtpReq(email, "123456", "newPassword123");
            user.setPassword("oldPassword");

            when(userService.getUserByEmail(req.getEmail())).thenReturn(user);
            when(otpService.validateOtp(req.getEmail(), req.getOtp(), OtpService.EPurpose.RESET_PASS)).thenReturn(true);
            doNothing().when(otpService).invalidateOtp(req.getEmail(), OtpService.EPurpose.RESET_PASS);
            when(passwordEncoder.encode(req.getNewPassword())).thenReturn("encodedNewPassword");
            when(userRepos.save(any(User.class))).thenReturn(user);

            // Act - Reset Password
            authService.resetPasswordWithOtp(req);

            // Assert - Reset Password
            verify(userService, times(2)).getUserByEmail(req.getEmail());
            verify(otpService, times(1)).validateOtp(req.getEmail(), req.getOtp(), OtpService.EPurpose.RESET_PASS);
            verify(otpService, times(1)).invalidateOtp(req.getEmail(), OtpService.EPurpose.RESET_PASS);
            verify(userRepos, times(1)).save(user);
            assertEquals("encodedNewPassword", user.getPassword());
        }
    }

    @Test
    void testForgotPassword_InvalidOtp_AUTH_FORGOT_PASS_NEG_001() {
        // Arrange - Generate OTP
        String email = "user@example.com";
        ResetPassWithOtpReq req = new ResetPassWithOtpReq(email, "wrongOtp", "newPassword123");
        User user = new User();
        user.setEmail(req.getEmail());

        when(userService.getUserByEmail(req.getEmail())).thenReturn(user);
        doThrow(new CustomException("Invalid OTP")).when(otpService).validateOtp(req.getEmail(), req.getOtp(), OtpService.EPurpose.RESET_PASS);

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authService.resetPasswordWithOtp(req)
        );

        assertEquals("Invalid OTP", exception.getMessage());
        verify(otpService, times(1)).validateOtp(req.getEmail(), req.getOtp(), OtpService.EPurpose.RESET_PASS);
        verify(userRepos, never()).save(any(User.class));
    }
}
