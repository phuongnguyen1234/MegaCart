package com.megacart.utils;

import java.security.SecureRandom;

public final class PasswordUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int PASSWORD_LENGTH = 8;

    // Private constructor để ngăn việc khởi tạo đối tượng từ lớp tiện ích
    private PasswordUtils() {}

    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}