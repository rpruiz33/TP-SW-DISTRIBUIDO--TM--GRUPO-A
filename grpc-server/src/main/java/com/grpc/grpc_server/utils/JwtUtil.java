package com.grpc.grpc_server.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Clave simetrica
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //Tiempo de expiracion
    private static final long EXPIRATION = 10000 * 60 * 60; // 10 hora

    public static String generateToken(String username, String roleName,String msg) {
        return Jwts.builder()
                .setSubject(username) //Set username
                .claim("role", roleName)
                .claim("message", msg) //Asigna el rol al token
                .setIssuedAt(new Date()) //Fecha de creacion del token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) //Fecha de expiracion del token
                .signWith(key) //La firma
                .compact(); //Genera el String
    }

    //Recibe un token y lo verifica
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Acceder directo al username
    public static String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    //Acceder directo al rol
    public static String getRole(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role");
    }

    //Acceder directo al message
    public static String getMessage(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("message");
    }
}