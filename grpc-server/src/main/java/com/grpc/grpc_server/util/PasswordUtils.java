package com.grpc.grpc_server.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public static String encryptPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }


     //Verifica si el password plano coincide con el hash almacenado.

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
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
