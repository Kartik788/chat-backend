package com.chat.chatbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    private final String url = "jdbc:mysql://localhost:3306/chatdb";
    private final String dbUsername = "root";
    private final String dbPassword = "kartik@123";

    @PostMapping("/login")
    public String loginUser(@RequestBody UserRequest request) {
        String username = request.getUsername();
        String plainPassword = request.getPassword();

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String query = "SELECT password, salt FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String storedSalt = rs.getString("salt");

                // Hash the entered password with stored salt
                String hashedInput = hashPassword(plainPassword, storedSalt);

                if (storedHashedPassword.equals(hashedInput)) {
                    return "User exists";
                } else {
                    return "Invalid username or password";
                }
            } else {
                return "User does not exist";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong on server";
        }
    }

    // Helper method to hash the password with salt
    private String hashPassword(String password, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String saltedPassword = password + salt;
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    public static class UserRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
