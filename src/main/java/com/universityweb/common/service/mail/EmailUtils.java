package com.universityweb.common.service.mail;

public class EmailUtils {

    public static String generateHtmlOtpTemplate(String purpose, String otp, String message) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>" + purpose + " OTP</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f9f9f9; }" +
                ".container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff; }" +
                "h2 { color: #333; text-align: center; }" +
                "p { font-size: 16px; color: #555; }" +
                ".otp-box { text-align: center; margin: 20px 0; }" +
                ".otp { display: inline-block; padding: 10px 20px; font-size: 24px; background-color: #B2F3F3; color: #008080; border-radius: 5px; }" +
                ".footer { font-size: 12px; text-align: center; color: #999; margin-top: 20px; }" +
                "hr { border: none; border-top: 1px solid #ddd; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>&#128274; " + purpose + " OTP</h2>" +
                "<p>" + message + "</p>" +
                "<div class='otp-box'>" +
                "<span class='otp' id='otp'>" + otp + "</span>" +
                "</div>" +
                "<p style='font-size: 14px; color: #777;'>If you did not request this, please ignore this email.</p>" +
                "<hr>" +
                "<footer class='footer'>© 2024 An & Hung. All rights reserved.</footer>" +
                "</div>" +

                "</body>" +
                "</html>";
    }
}
