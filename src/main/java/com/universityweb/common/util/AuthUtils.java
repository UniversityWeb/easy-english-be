package com.universityweb.common.util;

import com.universityweb.common.auth.request.UpdatePasswordRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AuthUtils {
    public static final int ERROR_TOO_SHORT = 1;
    public static final int ERROR_MISSING_UPPERCASE = 2;
    public static final int ERROR_MISSING_LOWERCASE = 3;
    public static final int ERROR_MISSING_DIGIT = 4;
    public static final int ERROR_MISSING_SPECIAL = 5;
    public static final int ERROR_PASSWORD_NO_MATCH = 6;
    public static final int PASSWORD_VALID = 0;

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final Map<Integer, String> ERROR_MESSAGES = new HashMap<>();

    static {
        ERROR_MESSAGES.put(ERROR_TOO_SHORT, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        ERROR_MESSAGES.put(ERROR_MISSING_UPPERCASE, "Password must contain at least one uppercase letter");
        ERROR_MESSAGES.put(ERROR_MISSING_LOWERCASE, "Password must contain at least one lowercase letter");
        ERROR_MESSAGES.put(ERROR_MISSING_DIGIT, "Password must contain at least one digit");
        ERROR_MESSAGES.put(ERROR_MISSING_SPECIAL, "Password must contain at least one special character");
        ERROR_MESSAGES.put(ERROR_PASSWORD_NO_MATCH, "Password and Confirm Password do not match");
    }

    public static int isValidPassAndConfirmPass(UpdatePasswordRequest request) {
        String password = request.password();
        String confirmPassword = request.confirmPassword();

        int result = isValidPassword(password);
        if (result == PASSWORD_VALID) {
            if (!password.equals(confirmPassword)) {
                return ERROR_PASSWORD_NO_MATCH;
            }
        }
        return PASSWORD_VALID;
    }

    public static int isValidPassword(String password) {
        if (!StringUtils.hasText(password) || password.length() < MIN_PASSWORD_LENGTH) {
            return ERROR_TOO_SHORT;
        }
        if (!password.matches(".*[A-Z].*")) {
            return ERROR_MISSING_UPPERCASE;
        }
        if (!password.matches(".*[a-z].*")) {
            return ERROR_MISSING_LOWERCASE;
        }
        if (!password.matches(".*\\d.*")) {
            return ERROR_MISSING_DIGIT;
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            return ERROR_MISSING_SPECIAL;
        }
        return PASSWORD_VALID;
    }

    public static void validPassAndConfirmPass(UpdatePasswordRequest request) {
        int errorCode = isValidPassAndConfirmPass(request);
        if (errorCode != PASSWORD_VALID) {
            throw new BadCredentialsException(ERROR_MESSAGES.get(errorCode));
        }
    }

    /**
     * Validates the password and throws an exception if invalid.
     *
     * @param password The password to validate.
     */
    public static void validatePass(String password) {
        int errorCode = isValidPassword(password);

        if (errorCode != PASSWORD_VALID) {
            throw new BadCredentialsException(ERROR_MESSAGES.get(errorCode));
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
