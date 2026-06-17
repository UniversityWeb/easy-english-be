package com.universityweb.common.auth.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
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
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.security.JwtGenerator;
import com.universityweb.common.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.universityweb.common.security.SecurityUtils;
import com.universityweb.common.auth.response.SessionResponse;
import com.universityweb.common.infrastructure.search.dto.FilterRequest;
import com.universityweb.common.infrastructure.search.dto.SearchRequest;
import com.universityweb.common.infrastructure.search.operator.FilterOperator;
import com.universityweb.common.infrastructure.search.specification.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

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
        boolean isExistsByUsername = userService.existsByUsername(username);
        if (isExistsByUsername) {
            String msg = "Username already exists";
            throw new UserAlreadyExistsException(msg);
        }

        String email = registerRequest.email();
        AuthUtils.validateEmail(email);

        boolean isExistsEmail = userService.existsByEmail(email);
        if (isExistsEmail) {
            String msg = "Email already exists";
            throw new CustomException(msg);
        }

        String plainPassword = registerRequest.password();
        AuthUtils.validatePass(plainPassword);

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

        User saved = userService.save(user);

        otpService.generateAndSendOtp(email, OtpService.EPurpose.ACTIVE_ACCOUNT);
        return uMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        return login(loginRequest, "Unknown Device", "127.0.0.1", "Unknown Location");
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest, String deviceInfo, String ipAddress, String loginLocation) {
        String usernameOrEmail = loginRequest.usernameOrEmail();
        String password = loginRequest.password();

        String username = AuthUtils.isValidEmail(usernameOrEmail)
                ? userRepos.getUsernameByEmail(usernameOrEmail)
                : usernameOrEmail;

        User user = userService.loadUserByUsername(username);

        LoginResponse statusResponse = handleAccountStatus(user);
        if (statusResponse != null) {
            return statusResponse;
        }

        Authentication authentication = authenticateUser(username, password);

        Token generatedToken = jwtGenerator.generateAndSaveToken(user, deviceInfo, ipAddress, loginLocation);
        return LoginResponse.builder()
                .message("Login successfully")
                .tokenType("Bearer")
                .tokenStr(generatedToken.getTokenStr())
                .refreshTokenStr(generatedToken.getRefreshTokenStr())
                .user(uMapper.toDTO(user))
                .accountStatus(User.EStatus.ACTIVE)
                .build();
    }

    @Override
    public void logout() {
        revokeAllDevices();
    }

    @Override
    @Transactional
    public void logoutDevice(String tokenStr) {
        if (tokenStr != null) {
            if (tokenStr.startsWith("Bearer ")) {
                tokenStr = tokenStr.substring(7);
            }
            tokenRepos.deleteByTokenStr(tokenStr);
        }
    }

    @Override
    @Transactional
    public void revokeAllDevices() {
        User user = getCurUser();
        tokenRepos.deleteByUserUsername(user.getUsername());
    }

    @Override
    public UserDTO getUserByTokenStr(String tokenStr) {
        if (!jwtGenerator.isValidToken(tokenStr)) {
            throw new BadCredentialsException("Invalid or expired token");
        }
        Token token = tokenRepos.findWithUserByTokenStr(tokenStr)
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
        return loginWithOtp(loginWithOtpRequest, "Unknown Device", "127.0.0.1", "Unknown Location");
    }

    @Override
    @Transactional
    public LoginResponse loginWithOtp(OtpRequest loginWithOtpRequest, String deviceInfo, String ipAddress, String loginLocation) {
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

        Token generatedToken = jwtGenerator.generateAndSaveToken(user, deviceInfo, ipAddress, loginLocation);
        return LoginResponse.builder()
                .message("OTP login successfully")
                .tokenType("Bearer")
                .tokenStr(generatedToken.getTokenStr())
                .refreshTokenStr(generatedToken.getRefreshTokenStr())
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
    public UserDTO resendOTPToActiveAccount(String usernameOrEmail) {
        String username = AuthUtils.isValidEmail(usernameOrEmail)
                ? userRepos.getUsernameByEmail(usernameOrEmail)
                : usernameOrEmail;

        User user = userService.loadUserByUsername(username);
        String email = user.getEmail();

        if (user.getStatus() == User.EStatus.ACTIVE) {
            String msg = "User account is already active. No OTP can be resent.";
            throw new UserAlreadyActiveException(msg);
        }

        if (user.getStatus() == User.EStatus.DELETED) {
            String msg = "User account is deleted. No OTP can be resent.";
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
        if (!AuthUtils.isValidEmail(email)) {
            throw new EmailNotFoundException("Email is not valid");
        }

        AuthUtils.validPassAndConfirmPass(request);

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
        if (email == null || email.isEmpty() || !AuthUtils.isValidEmail(email)) {
            throw new CustomException("Email is not valid");
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
        return loginWithGoogle(request, "Unknown Device", "127.0.0.1", "Unknown Location");
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(GoogleLoginRequest request, String deviceInfo, String ipAddress, String loginLocation) {
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
            Token generatedToken = jwtGenerator.generateAndSaveToken(existingUser, deviceInfo, ipAddress, loginLocation);
            return LoginResponse.builder()
                    .message("OTP login successfully")
                    .tokenType("Bearer")
                    .tokenStr(generatedToken.getTokenStr())
                    .refreshTokenStr(generatedToken.getRefreshTokenStr())
                    .user(uMapper.toDTO(existingUser))
                    .accountStatus(User.EStatus.ACTIVE)
                    .build();
        } catch (Exception e) {
            throw new CustomException("Error during verify Google Account: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenStr = request.refreshTokenStr();
        Token token = tokenRepos.findWithUserByRefreshTokenStr(refreshTokenStr)
                .orElseThrow(() -> new TokenNotFoundException("Invalid Refresh Token"));

        if (token.isUsed() || token.isRevoked()) {
            tokenRepos.deleteByUserUsername(token.getUser().getUsername());
            throw new SecurityException("Security Alert: Refresh Token reuse detected. All sessions revoked.");
        }

        if (token.getRefreshExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepos.delete(token);
            throw new ExpiredTokenException("Refresh Token has expired. Please login again.");
        }

        User user = token.getUser();

        token.setUsed(true);
        tokenRepos.save(token);

        LocalDateTime curTime = LocalDateTime.now();
        LocalDateTime expirationTime = curTime.plus(SecurityUtils.EXPIRATION_DURATION_MILLIS, ChronoUnit.MILLIS);
        String generatedToken = jwtGenerator.generateToken(user.getUsername(), curTime, expirationTime);

        String generatedRefreshToken = java.util.UUID.randomUUID().toString();
        LocalDateTime refreshExpirationTime = curTime.plus(7, ChronoUnit.DAYS);

        Token newToken = Token.builder()
                .tokenStr(generatedToken)
                .expiryDate(expirationTime)
                .refreshTokenStr(generatedRefreshToken)
                .refreshExpiryDate(refreshExpirationTime)
                .deviceInfo(token.getDeviceInfo())
                .ipAddress(token.getIpAddress())
                .loginLocation(token.getLoginLocation())
                .user(user)
                .used(false)
                .revoked(false)
                .build();

        tokenRepos.save(newToken);

        return LoginResponse.builder()
                .message("Token refreshed successfully")
                .tokenType("Bearer")
                .tokenStr(generatedToken)
                .refreshTokenStr(generatedRefreshToken)
                .user(uMapper.toDTO(user))
                .accountStatus(User.EStatus.ACTIVE)
                .build();
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

    @Override
    public Page<SessionResponse> getActiveSessions(String authHeader, SearchRequest searchRequest) {
        User user = getCurUser();
        String currentToken = authHeader != null ? authHeader.replace("Bearer ", "").trim() : "";

        // Enforce user scope and active sessions constraint
        FilterRequest userFilter = new FilterRequest();
        userFilter.setField("user.username");
        userFilter.setOperator(FilterOperator.EQ);
        userFilter.setValue(user.getUsername());

        FilterRequest revokedFilter = new FilterRequest();
        revokedFilter.setField("revoked");
        revokedFilter.setOperator(FilterOperator.EQ);
        revokedFilter.setValue(false);

        searchRequest.getFilters().add(userFilter);
        searchRequest.getFilters().add(revokedFilter);

        GenericSpecification<Token> spec = new GenericSpecification<>(searchRequest);

        List<Sort.Order> orders = searchRequest.getSort().stream()
                .map(s -> new Sort.Order(s.getDirection(), s.getField()))
                .collect(Collectors.toList());
        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);

        Page<Token> tokensPage = tokenRepos.findAll(spec, pageable);

        return tokensPage.map(t -> SessionResponse.builder()
                .id(t.getId())
                .deviceInfo(t.getDeviceInfo())
                .ipAddress(t.getIpAddress())
                .loginLocation(t.getLoginLocation())
                .expiryDate(t.getExpiryDate())
                .isCurrent(currentToken.equals(t.getTokenStr()))
                .build());
    }

    @Override
    @Transactional
    public void revokeSession(Long id) {
        User user = getCurUser();
        Token token = tokenRepos.findById(id).orElse(null);
        if (token != null && token.getUser().getUsername().equals(user.getUsername())) {
            tokenRepos.delete(token);
        }
    }
}