package com.universityweb.common.auth.service.otp;

import com.universityweb.common.auth.exception.ExpiredOtpException;
import com.universityweb.common.auth.exception.InvalidOtpException;
import com.universityweb.common.service.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {
    private static final ConcurrentHashMap<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    @Override
    public boolean validateOtp(String email, String otp, EPurpose purpose) {
        OtpRecord otpRecord = otpStore.get(email);

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
        otpStore.remove(email);
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
        }
    }

    private void sendOtpToLogin(String toEmail, String otp) {
        String subject = "Login - Your One-Time Password (OTP)";
        String htmlBody = "<h3>Your OTP is: " + otp + "</h3>" +
                "<p>Please use this code to complete your login. The code is valid for " +
                OtpService.OTP_EXPIRATION_MINUTES + " minutes.</p>";

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToActiveAccount(String toEmail, String otp) {
        String subject = "Account Activation - One-Time Password (OTP)";
        String htmlBody = "<h3>Your OTP for account activation is: " + otp + "</h3>" +
                "<p>Please use this code to activate your account. The OTP is valid for " +
                OtpService.OTP_EXPIRATION_MINUTES +
                " minutes from the time of receipt.</p>";

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    private void sendOtpToUpdateProfile(String toEmail, String otp) {
        String subject = "Update Profile - One-Time Password (OTP)";
        String htmlBody = "<h3>Your OTP for updating profile is: " + otp + "</h3>" +
                "<p>Please use this code to update your profile. The OTP is valid for " +
                OtpService.OTP_EXPIRATION_MINUTES +
                " minutes from the time of receipt.</p>";

        emailService.sendHtmlContent(toEmail, subject, htmlBody);
    }

    @Scheduled(fixedRate = 120_000)
    private void removeExpiredOtp() {
        Iterator<String> iterator = otpStore.keySet().iterator();

        while (iterator.hasNext()) {
            String email = iterator.next();
            OtpRecord otpRecord = otpStore.get(email);

            if (otpRecord != null && otpRecord.expiryTime().isAfter(LocalDateTime.now())) {
                iterator.remove();
            }
        }
    }

    private String generateOtp(String email, EPurpose purpose) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        OtpRecord otpRecord = new OtpRecord(otp, purpose, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        otpStore.put(email, otpRecord);

        return otp;
    }

    private record OtpRecord(
            String otp,
            EPurpose purpose,
            LocalDateTime expiryTime
    ) {}
}
