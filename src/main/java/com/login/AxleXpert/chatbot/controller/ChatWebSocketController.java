package com.login.AxleXpert.chatbot.controller;

import com.login.AxleXpert.chatbot.dto.ChatMessage;
import com.login.AxleXpert.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for real-time chatbot interactions
 * Handles WebSocket connections and message broadcasting
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatbotService chatbotService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle incoming chat messages via WebSocket
     * @param sessionId The session ID for the chat
     * @param message The chat message from the user
     */
    @MessageMapping("/chat/{sessionId}")
    public void handleChatMessage(@DestinationVariable String sessionId, ChatMessage message) {
        log.info("Received WebSocket message for session {}: {}", sessionId, message.getContent());

        // Ensure session ID is set
        message.setSessionId(sessionId);

        // Process the message and send response back to the specific session
        chatbotService.processMessage(message)
                .subscribe(
                    response -> {
                        log.info("Sending WebSocket response for session {}", sessionId);
                        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, response);
                    },
                    error -> {
                        log.error("Error processing WebSocket message for session {}: {}", sessionId, error.getMessage());
                        // Send error message back to client
                        ChatMessage errorMessage = new ChatMessage(
                                ChatMessage.MessageType.SYSTEM,
                                "Sorry, I encountered an error processing your message. Please try again.",
                                sessionId,
                                System.currentTimeMillis(),
                                "error"
                        );
                        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, errorMessage);
                    }
                );
    }

    /**
     * Handle user connection to chat
     * @param sessionId The session ID for the chat
     */
    @MessageMapping("/chat/connect/{sessionId}")
    @SendTo("/topic/chat/{sessionId}")
    public ChatMessage handleConnection(@DestinationVariable String sessionId) {
        log.info("User connected to chat session: {}", sessionId);

        // Send welcome message
        return chatbotService.getWelcomeMessage(sessionId);
    }

    /**
     * Handle typing indicator
     * @param sessionId The session ID for the chat
     */
    @MessageMapping("/chat/typing/{sessionId}")
    public void handleTyping(@DestinationVariable String sessionId) {
        log.debug("User typing in session: {}", sessionId);

        // Broadcast typing indicator to the session
        ChatMessage typingMessage = new ChatMessage(
                ChatMessage.MessageType.TYPING,
                "User is typing...",
                sessionId,
                System.currentTimeMillis(),
                "typing-indicator"
        );
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, typingMessage);
    }
}
