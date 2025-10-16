package com.example.userservice.util;


import java.security.SecureRandom;

public class OtpUtil {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10)); // 0-9
        }
        return otp.toString();
    }

}