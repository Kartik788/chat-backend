package com.chat.chatbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    private final String url = "jdbc:mysql://localhost:3306/chatdb"; // 🔁 change this
    private final String dbUsername = "root"; // 🔁 change this
    private final String dbPassword = "kartik@123"; // 🔁 change this

    @GetMapping("/users")
    public List<String> getAllUsers(@RequestParam String exclude) {
        List<String> users = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String query = "SELECT username FROM users WHERE username != ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, exclude);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}

