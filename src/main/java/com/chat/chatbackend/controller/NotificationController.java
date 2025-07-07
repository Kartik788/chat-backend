package com.chat.chatbackend.controller;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Called manually after message is saved to DB
    public void notifyReceiver(String receiverUsername) {
        messagingTemplate.convertAndSend("/topic/" + receiverUsername, "New message received");
    }
}
