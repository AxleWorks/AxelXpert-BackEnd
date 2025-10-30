package com.login.AxleXpert.chatbot.controller;

import com.login.AxleXpert.chatbot.dto.ChatMessage;
import com.login.AxleXpert.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for chatbot interactions
 * Provides HTTP endpoints for sending messages and getting responses
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:3001"})
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {

    private final ChatbotService chatbotService;

    /**
     * Send a message to the chatbot and get a response
     * @param message The chat message from the user
     * @return Bot response message
     */
    @PostMapping("/message")
    public Mono<ResponseEntity<ChatMessage>> sendMessage(@RequestBody ChatMessage message) {
        log.info("Received message for session {}: {}", message.getSessionId(), message.getContent());

        return chatbotService.processMessage(message)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Sent response for session {}", message.getSessionId()))
                .doOnError(error -> log.error("Error processing message: {}", error.getMessage()));
    }

    /**
     * Get welcome message for a new chat session
     * @param sessionId The session ID
     * @return Welcome message
     */
    @GetMapping("/welcome/{sessionId}")
    public ResponseEntity<ChatMessage> getWelcomeMessage(@PathVariable String sessionId) {
        log.info("Getting welcome message for session {}", sessionId);

        ChatMessage welcomeMessage = chatbotService.getWelcomeMessage(sessionId);
        return ResponseEntity.ok(welcomeMessage);
    }

    /**
     * Health check endpoint
     * @return Status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running");
    }
}
