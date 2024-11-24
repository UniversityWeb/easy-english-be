package com.universityweb.common.auth.service.auth;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.request.*;
import com.universityweb.common.auth.response.ActiveAccountResponse;
import com.universityweb.common.auth.response.LoginResponse;

public interface AuthService {

    UserDTO registerStudentAccount(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    void logout();

    UserDTO getUserByTokenStr(String tokenStr);

    String getCurrentUsername();

    void checkAuthorization(String targetUsername);

    UserDTO updateOwnPassword(UpdatePasswordRequest request);

    void generateAndSendOtpToLogin(LoginRequest loginRequest);

    LoginResponse loginWithOtp(OtpRequest loginWithOtpRequest);

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
}
