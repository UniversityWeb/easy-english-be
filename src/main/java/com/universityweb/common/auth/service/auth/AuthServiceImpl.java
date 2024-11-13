package com.universityweb.common.auth.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.universityweb.common.Utils;
import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.Token;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.*;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.TokenRepos;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.*;
import com.universityweb.common.auth.response.ActiveAccountResponse;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.service.GoogleAuthService;
import com.universityweb.common.auth.service.otp.OtpService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.security.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper uMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final AuthenticationManager authManager;
    private final OtpService otpService;
    private final UserRepos userRepos;
    private final TokenRepos tokenRepos;
    private final GoogleAuthService googleAuthService;

    @Override
    @Transactional
    public UserDTO registerStudentAccount(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        boolean isExists = userService.existsByUsername(username);
        if (isExists) {
            String msg = "Username already exists";
            throw new UserAlreadyExistsException(msg);
        }

        String email = registerRequest.email();
        String plainPassword = registerRequest.password();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .fullName(registerRequest.fullName())
                .email(email)
                .phoneNumber(registerRequest.phoneNumber())
                .bio("")
                .gender(registerRequest.gender())
                .dob(registerRequest.dob())
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .avatarPath(registerRequest.avatarPath())
                .status(User.EStatus.INACTIVE)
                .build();
        user.setPassword(encodedPassword);

        User saved = userService.save(user);

        otpService.generateAndSendOtp(email, OtpService.EPurpose.ACTIVE_ACCOUNT);
        return uMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        String usernameOrEmail = loginRequest.usernameOrEmail();
        String password = loginRequest.password();

        String username = Utils.isEmail(usernameOrEmail)
                ? userRepos.getUsernameByEmail(usernameOrEmail)
                : usernameOrEmail;

        User user = userService.loadUserByUsername(username);

        LoginResponse statusResponse = handleAccountStatus(user);
        if (statusResponse != null) {
            return statusResponse;
        }

        Authentication authentication = authenticateUser(username, password);

        String generatedToken = jwtGenerator.generateAndSaveToken(user);
        return LoginResponse.builder()
                .message("Login successfully")
                .tokenType("Bearer")
                .tokenStr(generatedToken)
                .user(uMapper.toDTO(user))
                .accountStatus(User.EStatus.ACTIVE)
                .build();
    }

    @Override
    public void logout() {
        User user = getCurUser();
        tokenRepos.deleteByUser(user);
    }

    @Override
    public UserDTO getUserByTokenStr(String tokenStr) {
        jwtGenerator.isValidToken(tokenStr);
        Token token = tokenRepos.findByTokenStr(tokenStr)
                .orElseThrow(() -> new TokenNotFoundException(tokenStr));
        return uMapper.toDTO(token.getUser());
    }

    @Override
    public String getCurrentUsername() {
        User user = getCurUser();
        return user.getUsername();
    }

    @Override
    public void checkAuthorization(String targetUsername) {
        String currentUsername = getCurrentUsername();
        if (targetUsername == null || !targetUsername.equals(currentUsername)) {
            String msg = "User not authorized to access or modify this information";
            throw new SecurityException(msg);
        }
    }

    @Override
    public UserDTO updateOwnPassword(UpdatePasswordRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new SecurityException("Passwords do not match");
        }

        User user = getCurUser();
        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);

        return saveUserAndConvertToDTO(user);
    }

    @Override
    public void generateAndSendOtpToLogin(LoginRequest loginRequest) {
        String username = loginRequest.username();
        User user = userService.loadUserByUsername(username);
        if (user.getStatus() != User.EStatus.ACTIVE) {
            throw new UserNotActiveException("User account is not active");
        }

        validateUsernameAndPassword(loginRequest);

        String email = userService.getEmailByUsername(loginRequest.username());
        if (email == null || email.isEmpty()) {
            throw new EmailNotFoundException("Email not found");
        }
        otpService.generateAndSendOtp(email, OtpService.EPurpose.LOGIN);
    }

    @Override
    @Transactional
    public LoginResponse loginWithOtp(OtpRequest loginWithOtpRequest) {
        String username = loginWithOtpRequest.username();
        String otp = loginWithOtpRequest.otp();
        User user = userService.loadUserByUsername(username);
        LoginResponse statusResponse = handleAccountStatus(user);
        if (statusResponse != null) {
            return statusResponse;
        }

        String email = user.getEmail();
        otpService.validateOtp(email, otp, OtpService.EPurpose.LOGIN);
        otpService.invalidateOtp(email, OtpService.EPurpose.LOGIN);

        String generatedToken = jwtGenerator.generateAndSaveToken(user);
        return LoginResponse.builder()
                .message("OTP login successfully")
                .tokenType("Bearer")
                .tokenStr(generatedToken)
                .user(uMapper.toDTO(user))
                .accountStatus(User.EStatus.ACTIVE)
                .build();
    }

    @Override
    public ActiveAccountResponse activateAccount(OtpRequest activeAccountRequest) {
        String username = activeAccountRequest.username();
        String email = userService.getEmailByUsername(username);
        String otp = activeAccountRequest.otp();

        otpService.validateOtp(email, otp, OtpService.EPurpose.ACTIVE_ACCOUNT);
        otpService.invalidateOtp(email, OtpService.EPurpose.ACTIVE_ACCOUNT);

        User user = userService.loadUserByUsername(username);
        user.setStatus(User.EStatus.ACTIVE);
        UserDTO savedUserDTO = saveUserAndConvertToDTO(user);;
        return new ActiveAccountResponse("User account activated successfully", savedUserDTO);
    }

    @Override
    public void generateAndSendOtpToUpdateProfile(String username) {
        checkAuthorization(username);
        User user = userService.loadUserByUsername(username);
        String email = user.getEmail();
        if (email == null || email.isEmpty()) {
            throw new EmailNotFoundException("Email not found");
        }

        otpService.generateAndSendOtp(email, OtpService.EPurpose.UPDATE_PROFILE);
    }

    @Override
    public UserDTO updateProfileWithOTP(
            UpdateProfileWithOTPRequest updateProfileRequest
    ) {
        String username = updateProfileRequest.getUsername();
        checkAuthorization(username);

        User user = userService.loadUserByUsername(username);
        String email = user.getEmail();
        String otp = updateProfileRequest.getOtp();

        otpService.validateOtp(email, otp, OtpService.EPurpose.UPDATE_PROFILE);
        otpService.invalidateOtp(email, OtpService.EPurpose.UPDATE_PROFILE);

        user.setFullName(updateProfileRequest.getFullName());
        user.setEmail(updateProfileRequest.getEmail());
        user.setPhoneNumber(updateProfileRequest.getPhoneNumber());
        user.setBio(updateProfileRequest.getBio());
        user.setGender(updateProfileRequest.getGender());
        user.setDob(updateProfileRequest.getDob());

        return saveUserAndConvertToDTO(user);
    }

    @Override
    public UserDTO resendOTPToActiveAccount(String username) {
        User user = userService.loadUserByUsername(username);
        String email = user.getEmail();

        if (user.getStatus() == User.EStatus.ACTIVE) {
            String msg = "User account is already active. No OTP can be resent.";
            throw new UserAlreadyActiveException(msg);
        }

        otpService.generateAndSendOtp(email, OtpService.EPurpose.ACTIVE_ACCOUNT);

        return saveUserAndConvertToDTO(user);
    }

    @Override
    public User getCurUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.loadUserByUsername(username);
    }

    @Override
    public void generateOtpToUpdatePassword(UpdatePasswordRequest request) {
        User user = getCurUser();
        String email = user.getEmail();
        if (email == null || email.isEmpty()) {
            throw new EmailNotFoundException("Email not found");
        }

        if (!isPasswordValid(request)) {
            throw new BadCredentialsException("Password is not valid");
        }

        otpService.generateAndSendOtp(email, OtpService.EPurpose.UPDATE_PASS);
    }

    @Override
    public void updatePasswordWithOtp(UpdatePassWithOtpReq req) {
        User user = getCurUser();
        String email = user.getEmail();

        otpService.validateOtp(email, req.getOtp(), OtpService.EPurpose.UPDATE_PASS);
        otpService.invalidateOtp(email, OtpService.EPurpose.UPDATE_PASS);

        String encodedPass = passwordEncoder.encode(req.getNewPassword());
        user.setPassword(encodedPass);
        userRepos.save(user);
    }

    @Override
    public void generateOtpToResetPassword(String email) {
        if (email == null || email.isEmpty() || !isValidEmail(email)) {
            throw new RuntimeException("Email is not valid");
        }
        userService.getUserByEmail(email);
        otpService.generateAndSendOtp(email, OtpService.EPurpose.RESET_PASS);
    }

    @Override
    public void resetPasswordWithOtp(ResetPassWithOtpReq req) {
        User user = userService.getUserByEmail(req.getEmail());
        String email = user.getEmail();

        otpService.validateOtp(email, req.getOtp(), OtpService.EPurpose.RESET_PASS);
        otpService.invalidateOtp(email, OtpService.EPurpose.RESET_PASS);

        String encodedPass = passwordEncoder.encode(req.getNewPassword());
        user.setPassword(encodedPass);
        userRepos.save(user);
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        String idToken = request.token();
        if (idToken == null || idToken.isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }

        try {
            GoogleIdToken.Payload payload = googleAuthService.verifyToken(idToken);
            RegisterRequest req = googleAuthService.getInfor(payload);
            User existingUser = userRepos.findByEmail(req.email())
                    .orElse(null);
            if (existingUser == null) {
                UserDTO userDTO = registerStudentAccount(req);
                return LoginResponse.builder()
                        .message("")
                        .user(userDTO)
                        .accountStatus(User.EStatus.INACTIVE)
                        .build();
            }

            LoginResponse statusResponse = handleAccountStatus(existingUser);
            if (statusResponse != null) {
                return statusResponse;
            }
            String generatedToken = jwtGenerator.generateAndSaveToken(existingUser);
            return LoginResponse.builder()
                    .message("OTP login successfully")
                    .tokenType("Bearer")
                    .tokenStr(generatedToken)
                    .user(uMapper.toDTO(existingUser))
                    .accountStatus(User.EStatus.ACTIVE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error during verify Google Account: ", e);
        }
    }


    private UserDTO saveUserAndConvertToDTO(User user) {
        User savedUser = userRepos.save(user);
        return uMapper.toDTO(savedUser);
    }

    private boolean validateUsernameAndPassword(LoginRequest loginRequest) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isPasswordValid(UpdatePasswordRequest request) {
        String password = request.password();
        String confirmPassword = request.confirmPassword();

        // Password validation rules
        if (!StringUtils.hasText(password) || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }

        // Confirm that password and confirmPassword match
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        return true;
    }

    private LoginResponse handleAccountStatus(User user) {
        if (user.getStatus() == User.EStatus.DELETED) {
            return LoginResponse.builder()
                    .message("Your account is deleted")
                    .tokenType(null)
                    .tokenStr(null)
                    .user(uMapper.toDTO(user))
                    .accountStatus(User.EStatus.DELETED)
                    .build();
        }
        if (user.getStatus() == User.EStatus.INACTIVE) {
            return LoginResponse.builder()
                    .message("Your account is inactive")
                    .tokenType(null)
                    .tokenStr(null)
                    .user(uMapper.toDTO(user))
                    .accountStatus(User.EStatus.INACTIVE)
                    .build();
        }
        return null;
    }

    private Authentication authenticateUser(String username, String password) {
        return authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
