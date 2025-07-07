package com.chat.chatbackend.controller;

import com.chat.chatbackend.model.Message;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class FetchMessageController {

    private final String url = "jdbc:mysql://localhost:3306/chatdb";
    private final String dbUsername = "root";
    private final String dbPassword = "kartik@123";

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam String sender, @RequestParam String receiver) {
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String query = "SELECT * FROM messages " +
                    "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                    "ORDER BY timestamp ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, receiver); // reverse check
            stmt.setString(4, sender);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message(
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getString("message"),
                        rs.getString("timestamp")
                );
                messages.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
