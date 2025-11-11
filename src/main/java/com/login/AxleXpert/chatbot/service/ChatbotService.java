package com.login.AxleXpert.chatbot.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.login.AxleXpert.Branches.dto.BranchDTO;
import com.login.AxleXpert.Services.dto.ServiceDTO;
import com.login.AxleXpert.Tasks.dto.TaskDTO;
import com.login.AxleXpert.Users.dto.UserDTO;
import com.login.AxleXpert.Vehicals.dto.VehicleDTO;
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
    private final BackendApiService backendApiService;

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

        // Always fetch context data if userId is provided
        String contextData = null;
        if (message.getUserId() != null) {
            contextData = getContextDataForQuery(userQuery, message.getUserId(), message.getAccessToken());
            log.info("Fetched context data for user {}: {} characters", message.getUserId(), 
                    contextData != null ? contextData.length() : 0);
        }

        // If we have context data, use it; otherwise fall back to RAG knowledge base
        String finalContext = contextData != null ? contextData : ragService.retrieveRelevantContext(userQuery);

        // Create RAG request with context
        RagRequest ragRequest = new RagRequest(userQuery, finalContext, sessionId);

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
            long currentTime = System.currentTimeMillis();
            long maxAge = 24 * 60 * 60 * 1000; // 24 hours

            sessionContexts.entrySet().removeIf(entry ->
                    currentTime - entry.getValue().getLastActivity() > maxAge);

            log.info("Cleaned up old sessions. Current active sessions: {}", sessionContexts.size());
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
     * Get session context for a specific session
     */
    public SessionContext getSessionContext(String sessionId) {
        return sessionContexts.get(sessionId);
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
                "Feel free to ask me anything about our services!";

        return new ChatMessage(
                ChatMessage.MessageType.BOT,
                welcomeText,
                sessionId,
                System.currentTimeMillis(),
                "welcome"
        );
    }

    /**
     * Get context data for data queries by calling backend APIs
     */
    private String getContextDataForQuery(String query, Long userId, String accessToken) {
        StringBuilder contextData = new StringBuilder();

        try {
            // Always include services data for context
            List<ServiceDTO> services = backendApiService.getAllServices(accessToken);
            if (services != null && !services.isEmpty()) {
                contextData.append("SERVICES:\n");
                for (ServiceDTO service : services) {
                    contextData.append("- ").append(service.getName());
                    if (service.getDescription() != null) {
                        contextData.append(": ").append(service.getDescription());
                    }
                    if (service.getPrice() != null) {
                        contextData.append(" (Price: $").append(service.getPrice()).append(")");
                    }
                    contextData.append("\n");
                }
                contextData.append("\n");
            }

            // Include branches data
            List<BranchDTO> branches = backendApiService.getAllBranches(accessToken);
            if (branches != null && !branches.isEmpty()) {
                contextData.append("BRANCHES:\n");
                for (BranchDTO branch : branches) {
                    contextData.append("- ").append(branch.getName());
                    if (branch.getAddress() != null) {
                        contextData.append(" at ").append(branch.getAddress());
                    }
                    if (branch.getPhone() != null) {
                        contextData.append(" (Phone: ").append(branch.getPhone()).append(")");
                    }
                    contextData.append("\n");
                }
                contextData.append("\n");
            }

            // Include user-specific data if userId is available
            if (userId != null) {
                // User's vehicles
                List<VehicleDTO> vehicles = backendApiService.getUserVehicles(userId, accessToken);
                if (vehicles != null && !vehicles.isEmpty()) {
                    contextData.append("USER VEHICLES:\n");
                    for (VehicleDTO vehicle : vehicles) {
                        contextData.append("- ").append(vehicle.getMake()).append(" ").append(vehicle.getModel());
                        if (vehicle.getYear() != null) {
                            contextData.append(" (").append(vehicle.getYear()).append(")");
                        }
                        if (vehicle.getPlateNumber() != null) {
                            contextData.append(" - License: ").append(vehicle.getPlateNumber());
                        }
                        contextData.append("\n");
                    }
                    contextData.append("\n");
                }

                // User's current tasks/services
                List<TaskDTO> tasks = backendApiService.getCustomerTasks(userId, accessToken);
                if (tasks != null && !tasks.isEmpty()) {
                    contextData.append("CURRENT SERVICES:\n");
                    for (TaskDTO task : tasks) {
                        contextData.append("- Service ID ").append(task.id());
                        if (task.title() != null) {
                            contextData.append(": ").append(task.title());
                        }
                        if (task.status() != null) {
                            contextData.append(" (Status: ").append(task.status()).append(")");
                        }
                        if (task.assignedEmployeeName() != null) {
                            contextData.append(" - Assigned to: ").append(task.assignedEmployeeName());
                        }
                        contextData.append("\n");
                    }
                    contextData.append("\n");
                }
            }

            // Include managers data
            List<UserDTO> managers = backendApiService.getAllManagers(accessToken);
            if (managers != null && !managers.isEmpty()) {
                contextData.append("MANAGERS:\n");
                for (UserDTO manager : managers) {
                    contextData.append("- ").append(manager.getUsername());
                    if (manager.getEmail() != null) {
                        contextData.append(" (Email: ").append(manager.getEmail()).append(")");
                    }
                    if (manager.getPhoneNumber() != null) {
                        contextData.append(" (Phone: ").append(manager.getPhoneNumber()).append(")");
                    }
                    contextData.append("\n");
                }
                contextData.append("\n");
            }

            return contextData.toString();

        } catch (Exception e) {
            log.error("Error getting context data for query: {}", e.getMessage());
            return null;
        }
    }
}
