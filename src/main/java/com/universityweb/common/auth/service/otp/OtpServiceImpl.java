package com.universityweb.common.auth.service.otp;

import com.universityweb.common.auth.entity.OTP;
import com.universityweb.common.auth.exception.ExpiredOtpException;
import com.universityweb.common.auth.exception.InvalidOtpException;
import com.universityweb.common.service.mail.EmailService;
import com.universityweb.common.service.mail.EmailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class OtpServiceImpl implements OtpService {
    private static final Logger log = LogManager.getLogger(OtpServiceImpl.class);
    private static final Duration OTP_TTL = Duration.ofMinutes(OTP_EXPIRATION_MINUTES);

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean validateOtp(String email, String otpStr, EPurpose purpose) {
        String key = generateKey(email, purpose);
        String json = (String) redisTemplate.opsForValue().get(key);

        if (json == null) {
            throw new InvalidOtpException("OTP not found for the given email: " + email);
        }

        OTP otp = OTP.fromJson(json);

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            redisTemplate.delete(key);
            throw new ExpiredOtpException("OTP has expired for the given email: " + email);
        }

        if (!passwordEncoder.matches(otpStr, otp.getOtpStr())) {
            throw new InvalidOtpException("Invalid OTP provided for the given email: " + email);
        }

        return true;
    }

    @Override
    public void invalidateOtp(String email, EPurpose purpose) {
        String key = generateKey(email, purpose);
        redisTemplate.delete(key);
    }

    @Override
    public void generateAndSendOtp(String email, EPurpose purpose) {
        String otp = generateOtp(email, purpose);
        switch (purpose) {
            case LOGIN -> sendOtpToLogin(email, otp);
            case ACTIVE_ACCOUNT -> sendOtpToActiveAccount(email, otp);
            case UPDATE_PROFILE -> sendOtpToUpdateProfile(email, otp);
            case UPDATE_PASS -> sendOtpToUpdatePass(email, otp);
            case RESET_PASS -> sendOtpToResetPassword(email, otp);
        }
    }

    private String generateOtp(String email, EPurpose purpose) {
        String otpStr = String.valueOf((int) (Math.random() * 900000) + 100000);
        String encodedOtpStr = passwordEncoder.encode(otpStr);
        log.info("Generated OTP for {}: {}", email, otpStr);

        String key = generateKey(email, purpose);
        OTP otp = OTP.builder()
                .otpStr(encodedOtpStr)
                .expiryTime(LocalDateTime.now().plus(OTP_TTL))
                .build();
        String otpJson = otp.toJson();

        redisTemplate.opsForValue().set(key, otpJson, OTP_TTL.toSeconds(), TimeUnit.SECONDS);

        return otpStr;
    }

    private String generateKey(String email, EPurpose purpose) {
        return "otp:" + email + ":" + purpose.name();
    }

    private void sendOtpToResetPassword(String email, String otp) {
        String subject = "Reset Password - Your One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Reset Password", otp,
                "Please use this code to reset your password. This code is valid for " +
                        OTP_EXPIRATION_MINUTES + " minutes.");
        emailService.sendHtmlContent(email, subject, htmlBody);
    }

    private void sendOtpToUpdatePass(String toEmail, String otp) {
        String subject = "Update Password - Your One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Update Password", otp,
                "Please use this code to update your password. This code is valid for " +
                        OTP_EXPIRATION_MINUTES + " minutes.");
        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToLogin(String toEmail, String otp) {
        String subject = "Login - Your One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Login", otp,
                "Please use this code to complete your login. This code is valid for " +
                        OTP_EXPIRATION_MINUTES + " minutes.");
        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToActiveAccount(String toEmail, String otp) {
        String subject = "Account Activation - One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Account Activation", otp,
                "Please use this code to activate your account. The OTP is valid for " +
                        OTP_EXPIRATION_MINUTES + " minutes.");
        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToUpdateProfile(String toEmail, String otp) {
        String subject = "Update Profile - One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Update Profile", otp,
                "Please use this code to update your profile. The OTP is valid for " +
                        OTP_EXPIRATION_MINUTES + " minutes.");
        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }
}