package com.universityweb.common.auth.service.otp;

import com.universityweb.common.auth.exception.ExpiredOtpException;
import com.universityweb.common.auth.exception.InvalidOtpException;
import com.universityweb.common.service.mail.EmailService;
import com.universityweb.common.service.mail.EmailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {
    private static Logger log = LogManager.getLogger(OtpServiceImpl.class);
    private static final Map<String, OtpRecord> otpCache = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    @Override
    public boolean validateOtp(String email, String otp, EPurpose purpose) {
        String key = generateKey(email, purpose);
        OtpRecord otpRecord = otpCache.get(key);

        if (otpRecord == null) {
            throw new InvalidOtpException("OTP not found for the given email: " + email);
        }

        if (otpRecord.expiryTime().isBefore(LocalDateTime.now())) {
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
        otpCache.remove(key);
    }

    @Override
    public void generateAndSendOtp(String email, EPurpose purpose) {
        String otp = generateOtp(email, purpose);
        switch (purpose) {
            case LOGIN:
                sendOtpToLogin(email, otp);
                break;
            case ACTIVE_ACCOUNT:
                sendOtpToActiveAccount(email, otp);
                break;
            case UPDATE_PROFILE:
                sendOtpToUpdateProfile(email, otp);
                break;
            case UPDATE_PASS:
                sendOtpToUpdatePass(email, otp);
                break;
            case RESET_PASS:
                sendOtpToResetPassword(email, otp);
                break;
        }
    }

    private void sendOtpToResetPassword(String email, String otp) {
        String subject = "Reset Password - Your One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Reset Password", otp,
                "Please use this code to reset your password. This code is valid for " +
                        OtpService.OTP_EXPIRATION_MINUTES + " minutes.");

        emailService.sendHtmlContent(email, subject, htmlBody);
    }

    private void sendOtpToUpdatePass(String toEmail, String otp) {
        String subject = "Update Password - Your One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Update Password", otp,
                "Please use this code to update your password. This code is valid for " +
                        OtpService.OTP_EXPIRATION_MINUTES + " minutes.");

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToLogin(String toEmail, String otp) {
        String subject = "Login - Your One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Login", otp,
                "Please use this code to complete your login. This code is valid for " +
                        OtpService.OTP_EXPIRATION_MINUTES + " minutes.");

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToActiveAccount(String toEmail, String otp) {
        String subject = "Account Activation - One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Account Activation", otp,
                "Please use this code to activate your account. The OTP is valid for " +
                        OtpService.OTP_EXPIRATION_MINUTES + " minutes.");

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToUpdateProfile(String toEmail, String otp) {
        String subject = "Update Profile - One-Time Password (OTP)";
        String htmlBody = EmailUtils.generateHtmlOtpTemplate("Update Profile", otp,
                "Please use this code to update your profile. The OTP is valid for " +
                        OtpService.OTP_EXPIRATION_MINUTES + " minutes.");

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private String generateKey(String email, EPurpose purpose) {
        return email + "_" + purpose;
    }

    @Scheduled(fixedRate = 3_600_000)
    private void removeExpiredOtp() {
        Iterator<String> iterator = otpCache.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            OtpRecord otpRecord = otpCache.get(key);

            if (otpRecord != null && otpRecord.expiryTime().isBefore(LocalDateTime.now())) {
                iterator.remove();
            }
        }
    }

    private String generateOtp(String email, EPurpose purpose) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        log.info("Generated OTP for {}: {}", email, otp);

        OtpRecord otpRecord = new OtpRecord(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        String key = generateKey(email, purpose);
        otpCache.put(key, otpRecord);

        return otp;
    }

    private record OtpRecord(
            String otp,
            LocalDateTime expiryTime
    ) {}
}
