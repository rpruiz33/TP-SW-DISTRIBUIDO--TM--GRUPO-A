package com.grpc.grpc_server.utils;

import java.security.SecureRandom;  // Para random seguro
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String encryptPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }


     //Verifica si el password plano coincide con el hash almacenado.

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    //Genera una password random
    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }

    // Test rápido
    public static void main(String[] args) {
        String raw = "1234";
        String hash = encryptPassword(raw);
        System.out.println("Raw: " + raw);
        System.out.println("Hash: " + hash);
        System.out.println("Check: " + checkPassword("1234", hash)); // true
        System.out.println("Check wrong: " + checkPassword("abcd", hash)); // false
    }
}
