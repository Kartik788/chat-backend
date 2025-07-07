package com.chat.chatbackend.controller;

import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/discovery")
@CrossOrigin(origins = "*")  // You can restrict this to your frontend domain later
public class DiscoveryController {

    private final String url = "jdbc:mysql://localhost:3306/chatdb";
    private final String dbUsername = "root";
    private final String dbPassword = "kartik@123";

    @GetMapping("/new-users")
    public List<String> getNewUsers(@RequestParam String user) {
        List<String> newUsers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {

            // Get all users except current
            String allUsersQuery = "SELECT username FROM users WHERE username != ?";
            PreparedStatement allUsersStmt = conn.prepareStatement(allUsersQuery);
            allUsersStmt.setString(1, user);
            ResultSet allRs = allUsersStmt.executeQuery();

            Set<String> allUsers = new HashSet<>();
            while (allRs.next()) {
                allUsers.add(allRs.getString("username"));
            }

            // Get all users current user has chatted with
            String chattedQuery = "SELECT DISTINCT sender, receiver FROM messages WHERE sender = ? OR receiver = ?";
            PreparedStatement chattedStmt = conn.prepareStatement(chattedQuery);
            chattedStmt.setString(1, user);
            chattedStmt.setString(2, user);
            ResultSet chattedRs = chattedStmt.executeQuery();

            Set<String> chattedUsers = new HashSet<>();
            while (chattedRs.next()) {
                String sender = chattedRs.getString("sender");
                String receiver = chattedRs.getString("receiver");

                if (!sender.equals(user)) chattedUsers.add(sender);
                if (!receiver.equals(user)) chattedUsers.add(receiver);
            }

            // Remaining = allUsers - chattedUsers
            for (String u : allUsers) {
                if (!chattedUsers.contains(u)) {
                    newUsers.add(u);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newUsers;
    }

}
