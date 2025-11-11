package com.login.AxleXpert.chatbot.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Session Context for maintaining conversation state in memory
 * This class stores the conversation history for each chat session
 * without persisting to database as requested
 */
@Data
@Slf4j
public class SessionContext {

    private final String sessionId;
    private final long createdAt;
    private long lastActivity;
    private final List<String> userMessages;
    private final List<String> botMessages;
    private int messageCount;
    private String userPreferences;

    /**
     * Constructor for new session
     */
    public SessionContext(String sessionId) {
        this.sessionId = sessionId;
        this.createdAt = System.currentTimeMillis();
        this.lastActivity = System.currentTimeMillis();
        this.userMessages = new ArrayList<>();
        this.botMessages = new ArrayList<>();
        this.messageCount = 0;

        log.debug("Created new session context for session: {}", sessionId);
    }

    /**
     * Add user message to conversation history
     */
    public void addUserMessage(String message) {
        userMessages.add(message);
        messageCount++;
        updateLastActivity();

        // Keep only last 20 messages to prevent memory issues
        if (userMessages.size() > 20) {
            userMessages.remove(0);
        }

        log.debug("Added user message to session {}: {}", sessionId, message);
    }

    /**
     * Add bot response to conversation history
     */
    public void addBotMessage(String message) {
        botMessages.add(message);
        updateLastActivity();

        // Keep only last 20 messages to prevent memory issues
        if (botMessages.size() > 20) {
            botMessages.remove(0);
        }

        log.debug("Added bot message to session {}", sessionId);
    }

    /**
     * Update last activity timestamp
     */
    private void updateLastActivity() {
        this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Get conversation context for AI (last few exchanges)
     */
    public String getConversationContext() {
        StringBuilder context = new StringBuilder();

        int maxMessages = Math.min(5, Math.min(userMessages.size(), botMessages.size()));

        for (int i = Math.max(0, userMessages.size() - maxMessages); i < userMessages.size(); i++) {
            if (i < botMessages.size()) {
                context.append("User: ").append(userMessages.get(i)).append("\n");
                context.append("Assistant: ").append(botMessages.get(i)).append("\n");
            }
        }

        return context.toString();
    }

    /**
     * Check if session is active (has recent activity)
     */
    public boolean isActive() {
        long currentTime = System.currentTimeMillis();
        long inactiveThreshold = 30 * 60 * 1000; // 30 minutes

        return (currentTime - lastActivity) < inactiveThreshold;
    }

    /**
     * Get session duration in minutes
     */
    public long getSessionDurationMinutes() {
        return (lastActivity - createdAt) / (60 * 1000);
    }

    /**
     * Get last user message
     */
    public String getLastUserMessage() {
        return userMessages.isEmpty() ? null : userMessages.get(userMessages.size() - 1);
    }

    /**
     * Get last bot message
     */
    public String getLastBotMessage() {
        return botMessages.isEmpty() ? null : botMessages.get(botMessages.size() - 1);
    }

    /**
     * Clear conversation history
     */
    public void clearHistory() {
        userMessages.clear();
        botMessages.clear();
        messageCount = 0;
        updateLastActivity();

        log.debug("Cleared history for session: {}", sessionId);
    }
}
