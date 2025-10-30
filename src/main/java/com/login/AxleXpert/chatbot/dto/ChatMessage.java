package com.login.AxleXpert.chatbot.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO for chat messages exchanged between client and server
 * This represents both incoming user messages and outgoing bot responses
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    /**
     * Type of message - USER for user messages, BOT for bot responses, SYSTEM for system messages
     */
    private MessageType type;

    /**
     * The actual message content
     */
    private String content;

    /**
     * Unique session ID to track conversation context
     */
    private String sessionId;

    /**
     * Timestamp when message was created
     */
    private long timestamp;

    /**
     * Optional metadata for additional information
     */
    private String metadata;

    /**
     * Message types enum
     */
    public enum MessageType {
        USER,    // Message from user
        BOT,     // Response from chatbot
        SYSTEM,  // System messages (connection status, errors, etc.)
        TYPING   // Typing indicator
    }

    /**
     * Constructor for simple user messages
     */
    public ChatMessage(MessageType type, String content, String sessionId) {
        this.type = type;
        this.content = content;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
    }
}
