package com.login.AxleXpert.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for Real-time Chat
 * This configuration enables STOMP messaging over WebSocket for live chat functionality
 * Clients can connect to /ws/chat endpoint and subscribe to topics for real-time updates
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${chatbot.websocket.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    /**
     * Configure message broker for handling chat messages
     * Sets up topic-based messaging for broadcasting responses
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple message broker for topics
        config.enableSimpleBroker("/topic", "/queue");

        // Set application destination prefix for client messages
        config.setApplicationDestinationPrefixes("/app");

        // Set user destination prefix for private messages
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints for WebSocket connections
     * Clients will connect to /ws/chat endpoint
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the chat endpoint with SockJS fallback
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins(allowedOrigins.split(","))
                .withSockJS()
                .setHeartbeatTime(25000) // 25 seconds heartbeat
                .setDisconnectDelay(30000); // 30 seconds disconnect delay

        // Register direct WebSocket endpoint (without SockJS)
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins(allowedOrigins.split(","));
    }
}
