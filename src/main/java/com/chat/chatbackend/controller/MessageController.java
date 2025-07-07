package com.chat.chatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final String url = "jdbc:mysql://localhost:3306/chatdb";
    private final String dbUsername = "root";
    private final String dbPassword = "kartik@123";

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // for WebSocket notification

    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatRequest chat) {
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String sql = "INSERT INTO messages (sender, receiver, message, timestamp) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, chat.getSender());
            stmt.setString(2, chat.getReceiver());
            stmt.setString(3, chat.getMessage());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, now);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Notify the receiver using WebSocket
                messagingTemplate.convertAndSend("/topic/" + chat.getReceiver(), "new-message");
                return "Message sent successfully";
            } else {
                return "Failed to send message";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error occurred while sending message";
        }
    }


    // Inner class for request payload
    public static class ChatRequest {
        private String sender;
        private String receiver;
        private String message;

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }

        public String getReceiver() { return receiver; }
        public void setReceiver(String receiver) { this.receiver = receiver; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
