package com.login.AxleXpert.chatbot.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.chatbot.dto.ChatMessage;
import com.login.AxleXpert.chatbot.dto.RagRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Main Chatbot Service that orchestrates RAG and AI services
 * This service handles the complete chatbot workflow:
 * 1. Receives user messages
 * 2. Retrieves relevant context using RAG
 * 3. Generates AI responses using Gemini
 * 4. Maintains session context (in memory, not database)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final RagService ragService;
    private final GeminiAiService geminiAiService;

    // In-memory storage for session context (not persisted to database)
    private final Map<String, SessionContext> sessionContexts = new ConcurrentHashMap<>();

    /**
     * Process a user message and generate an AI response
     * @param message The user's chat message
     * @return Mono containing the bot's response
     */
    public Mono<ChatMessage> processMessage(ChatMessage message) {
        String sessionId = message.getSessionId();
        String userQuery = message.getContent();

        log.info("Processing message for session {}: {}", sessionId, userQuery);

        // Update session context
        updateSessionContext(sessionId, userQuery);

        // Handle special commands
        if (isSpecialCommand(userQuery)) {
            return handleSpecialCommand(userQuery, sessionId);
        }

        // Retrieve relevant context from knowledge base
        String relevantContext = ragService.retrieveRelevantContext(userQuery);

        // Create RAG request
        RagRequest ragRequest = new RagRequest(userQuery, relevantContext, sessionId);

        // Generate response using Gemini AI
        return geminiAiService.generateResponse(ragRequest)
                .map(aiResponse -> {
                    // Update session with bot response
                    updateSessionWithBotResponse(sessionId, aiResponse);

                    return new ChatMessage(
                            ChatMessage.MessageType.BOT,
                            aiResponse,
                            sessionId,
                            System.currentTimeMillis(),
                            "rag-enhanced"
                    );
                })
                .doOnSuccess(response -> log.info("Generated response for session {}", sessionId))
                .doOnError(error -> log.error("Error processing message for session {}: {}",
                        sessionId, error.getMessage()));
    }

    /**
     * Update session context with user message
     */
    private void updateSessionContext(String sessionId, String userMessage) {
        SessionContext context = sessionContexts.computeIfAbsent(sessionId,
                k -> new SessionContext(sessionId));
        context.addUserMessage(userMessage);

        // Clean up old sessions (keep only last 100 sessions)
        if (sessionContexts.size() > 100) {
            cleanupOldSessions();
        }
    }

    /**
     * Update session context with bot response
     */
    private void updateSessionWithBotResponse(String sessionId, String botResponse) {
        SessionContext context = sessionContexts.get(sessionId);
        if (context != null) {
            context.addBotMessage(botResponse);
        }
    }

    /**
     * Check if the message is a special command
     */
    private boolean isSpecialCommand(String message) {
        String lowerMessage = message.toLowerCase().trim();
        return lowerMessage.equals("/help") ||
               lowerMessage.equals("/services") ||
               lowerMessage.equals("/locations") ||
               lowerMessage.equals("/hours") ||
               lowerMessage.equals("/contact");
    }

    /**
     * Handle special commands with predefined responses
     */
    private Mono<ChatMessage> handleSpecialCommand(String command, String sessionId) {
        String response;

        switch (command.toLowerCase().trim()) {
            case "/help":
                response = getHelpMessage();
                break;
            case "/services":
                response = getServicesMessage();
                break;
            case "/locations":
                response = getLocationsMessage();
                break;
            case "/hours":
                response = getHoursMessage();
                break;
            case "/contact":
                response = getContactMessage();
                break;
            default:
                response = "Unknown command. Type /help for available commands.";
        }

        return Mono.just(new ChatMessage(
                ChatMessage.MessageType.BOT,
                response,
                sessionId,
                System.currentTimeMillis(),
                "command-response"
        ));
    }

    /**
     * Generate welcome message for new sessions
     */
    public ChatMessage getWelcomeMessage(String sessionId) {
        String welcomeText = "👋 **Welcome to AxleXpert!** I'm your AI assistant here to help with all your vehicle service needs.\n\n" +
                "I can help you with:\n" +
                "- Service information and pricing\n" +
                "- Booking appointments\n" +
                "- Finding branch locations\n" +
                "- Vehicle maintenance advice\n" +
                "- Service status updates\n\n" +
                "Feel free to ask me anything about our services, or type `/help` for quick commands!";

        return new ChatMessage(
                ChatMessage.MessageType.BOT,
                welcomeText,
                sessionId,
                System.currentTimeMillis(),
                "welcome"
        );
    }

    /**
     * Get session context for a specific session
     */
    public SessionContext getSessionContext(String sessionId) {
        return sessionContexts.get(sessionId);
    }

    /**
     * Clean up old sessions to prevent memory leaks
     */
    private void cleanupOldSessions() {
        long currentTime = System.currentTimeMillis();
        long maxAge = 24 * 60 * 60 * 1000; // 24 hours

        sessionContexts.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getLastActivity() > maxAge);

        log.info("Cleaned up old sessions. Current active sessions: {}", sessionContexts.size());
    }

    // Predefined response methods for special commands

    private String getHelpMessage() {
        return "🔧 **AxleXpert Help Commands**\n\n" +
                "- `/services` - View our service offerings\n" +
                "- `/locations` - Find our branch locations\n" +
                "- `/hours` - See our operating hours\n" +
                "- `/contact` - Get contact information\n\n" +
                "You can also ask me natural questions like:\n" +
                "- \"How much does an oil change cost?\"\n" +
                "- \"What services do you offer?\"\n" +
                "- \"Where is your nearest location?\"\n" +
                "- \"How do I book an appointment?\"";
    }

    private String getServicesMessage() {
        return "🔧 **Our Services**\n\n" +
                "- **Oil Change** - From $30\n" +
                "- **Brake Service** - From $80\n" +
                "- **Tire Service** - From $50\n" +
                "- **Engine Diagnostics** - From $100\n" +
                "- **Transmission Service** - From $120\n" +
                "- **AC Service** - From $70\n" +
                "- **Battery Service** - From $90\n" +
                "- **Electrical Diagnostics** - From $85\n\n" +
                "For detailed information about any service, just ask!";
    }

    private String getLocationsMessage() {
        return "📍 **Our Locations**\n\n" +
                "- **Downtown Branch** - 123 Main Street\n" +
                "- **North Branch** - 456 Oak Avenue\n" +
                "- **South Branch** - 789 Pine Road\n" +
                "- **West Branch** - 321 Elm Street\n" +
                "- **East Branch** - 654 Maple Drive\n\n" +
                "All branches offer the same high-quality services!";
    }

    private String getHoursMessage() {
        return "🕒 **Operating Hours**\n\n" +
                "- **Monday - Friday**: 8:00 AM - 6:00 PM\n" +
                "- **Saturday**: 9:00 AM - 4:00 PM\n" +
                "- **Sunday**: Closed\n\n" +
                "*Emergency services available 24/7!*";
    }

    private String getContactMessage() {
        return "📞 **Contact Information**\n\n" +
                "- **Phone**: 1-800-AXLEXPERT (1-800-295-3977)\n" +
                "- **Email**: support@axlexpert.com\n" +
                "- **Emergency**: 1-800-EMERGENCY\n" +
                "- **Live Chat**: Available during business hours\n\n" +
                "*We're here to help 24/7!*";
    }
}
