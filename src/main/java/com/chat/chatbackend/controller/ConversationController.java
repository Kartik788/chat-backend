package com.chat.chatbackend.controller;

import com.chat.chatbackend.model.ConversationDTO;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class ConversationController {

    private final String url = "jdbc:mysql://localhost:3306/chatdb";
    private final String dbUsername = "root";
    private final String dbPassword = "kartik@123";

    @GetMapping("/conversations")
    public List<ConversationDTO> getUserConversations(@RequestParam String user) {
        List<ConversationDTO> conversations = new ArrayList<>();

        String query = """
                SELECT 
                    CASE 
                        WHEN sender = ? THEN receiver
                        ELSE sender
                    END AS other_user,
                    message,
                    timestamp
                FROM messages
                WHERE sender = ? OR receiver = ?
                ORDER BY timestamp DESC
                """;

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user);
            stmt.setString(2, user);
            stmt.setString(3, user);

            ResultSet rs = stmt.executeQuery();

            // Use LinkedHashMap to maintain insertion order and prevent duplicates
            Map<String, ConversationDTO> seenUsers = new LinkedHashMap<>();

            while (rs.next()) {
                String otherUser = rs.getString("other_user");
                if (!seenUsers.containsKey(otherUser)) {
                    seenUsers.put(otherUser, new ConversationDTO(
                            otherUser,
                            rs.getString("message"),
                            rs.getString("timestamp")
                    ));
                }
            }

            conversations.addAll(seenUsers.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conversations;
    }
}
