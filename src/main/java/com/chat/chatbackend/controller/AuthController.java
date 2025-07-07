package com.chat.chatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/chatdb";
    private final String dbUser = "root";
    private final String dbPassword = "kartik@123";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/signup")
    public String signup(@RequestBody UserRequest request) {
        String username = request.getUsername();
        String plainPassword = request.getPassword();

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {

            // Check if username exists
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return "Username already exists";
                }
            }

            // Generate salt
            String salt = generateSalt();

            // Hash password with salt
            String hashedPassword = hashPassword(plainPassword, salt);

            // Store user with hashed password and salt
            String insertQuery = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword);
                insertStmt.setString(3, salt);
                insertStmt.executeUpdate();
            }

            // 🔔 Notify all clients (no username included)
            messagingTemplate.convertAndSend("/topic/new-user", "new-user");

            return "Signup successful";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during signup";
        }
    }

    // Generate a random salt
    private String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash password with salt using SHA-256
    private String hashPassword(String password, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String saltedPassword = password + salt;
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    // Inner class for request body
    public static class UserRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
