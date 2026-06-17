package com.universityweb.common.auth.service.auth;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.request.*;
import com.universityweb.common.auth.response.ActiveAccountResponse;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.response.SessionResponse;
import com.universityweb.common.infrastructure.search.dto.SearchRequest;
import org.springframework.data.domain.Page;

public interface AuthService {

    UserDTO registerStudentAccount(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);
    LoginResponse login(LoginRequest loginRequest, String deviceInfo, String ipAddress, String loginLocation);

    void logout();
    void logoutDevice(String tokenStr);
    void revokeAllDevices();

    UserDTO getUserByTokenStr(String tokenStr);

    String getCurrentUsername();

    void checkAuthorization(String targetUsername);

    UserDTO updateOwnPassword(UpdatePasswordRequest request);

    void generateAndSendOtpToLogin(LoginRequest loginRequest);

    LoginResponse loginWithOtp(OtpRequest loginWithOtpRequest);
    LoginResponse loginWithOtp(OtpRequest loginWithOtpRequest, String deviceInfo, String ipAddress, String loginLocation);

    ActiveAccountResponse activateAccount(OtpRequest activeAccountRequest);

    void generateAndSendOtpToUpdateProfile(String username);

    UserDTO updateProfileWithOTP(UpdateProfileWithOTPRequest updateProfileRequest);

    UserDTO resendOTPToActiveAccount(String username);

    User getCurUser();

    void generateOtpToUpdatePassword(UpdatePasswordRequest request);

    void updatePasswordWithOtp(UpdatePassWithOtpReq updatePassWithOtpReq);

    void generateOtpToResetPassword(String email);

    void resetPasswordWithOtp(ResetPassWithOtpReq req);

    LoginResponse loginWithGoogle(GoogleLoginRequest req);
    LoginResponse loginWithGoogle(GoogleLoginRequest req, String deviceInfo, String ipAddress, String loginLocation);

    LoginResponse refreshToken(RefreshTokenRequest request);

    Page<SessionResponse> getActiveSessions(String authHeader, SearchRequest searchRequest);

    void revokeSession(Long id);
}
