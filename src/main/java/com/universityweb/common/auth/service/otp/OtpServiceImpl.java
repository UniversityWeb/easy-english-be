package com.universityweb.common.auth.service.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.universityweb.common.auth.exception.ExpiredOtpException;
import com.universityweb.common.auth.exception.InvalidOtpException;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.service.mail.EmailService;
import com.universityweb.common.service.mail.EmailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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

    @Override
    public boolean validateOtp(String email, String otp, EPurpose purpose) {
        String key = generateKey(email, purpose);
        String json = (String) redisTemplate.opsForValue().get(key);

        if (json == null) {
            throw new InvalidOtpException("OTP not found for the given email: " + email);
        }

        OtpRecord otpRecord = OtpRecord.fromJson(json);

        if (otpRecord.expiryTime().isBefore(LocalDateTime.now())) {
            redisTemplate.delete(key);
            throw new ExpiredOtpException("OTP has expired for the given email: " + email);
        }

        if (!otpRecord.otp().equals(otp)) {
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
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        log.info("Generated OTP for {}: {}", email, otp);

        OtpRecord otpRecord = new OtpRecord(otp, LocalDateTime.now().plus(OTP_TTL));
        String key = generateKey(email, purpose);

        redisTemplate.opsForValue().set(key, otpRecord.toJson(), OTP_TTL.toSeconds(), TimeUnit.SECONDS);

        return otp;
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

    /**
     * Serializable record class for Redis.
     */
    public static class OtpRecord implements Serializable {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpRecord(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String otp() {
            return otp;
        }

        public LocalDateTime expiryTime() {
            return expiryTime;
        }

        public String toJson() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            try {
                return objectMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to convert OtpRecord to JSON", e);
            }
        }

        public static OtpRecord fromJson(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(json, OtpRecord.class);
            } catch (Exception e) {
                throw new RuntimeException("Deserialization failed", e);
            }
        }
    }
}