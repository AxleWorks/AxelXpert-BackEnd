package com.login.AxleXpert.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.login.AxleXpert.chatbot.dto.RagRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini AI Service for handling LLM interactions
 * This service communicates with Google's Gemini API to generate
 * contextually aware responses using RAG (Retrieval-Augmented Generation)
 */
@Service
@Slf4j
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiAiService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate a response using Gemini API with RAG context
     * @param ragRequest Contains the user query and retrieved context
     * @return Generated response from Gemini
     */
    public Mono<String> generateResponse(RagRequest ragRequest) {
        try {
            // Create the prompt with RAG context
            String enhancedPrompt = createRagPrompt(ragRequest.getQuery(), ragRequest.getContext());

            // Build the request payload for Gemini API
            Map<String, Object> requestBody = buildGeminiRequest(enhancedPrompt, ragRequest);

            log.debug("Sending request to Gemini API for session: {}", ragRequest.getSessionId());

            return webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::extractResponseFromGemini)
                    .doOnSuccess(response -> log.debug("Received response from Gemini for session: {}",
                            ragRequest.getSessionId()))
                    .doOnError(error -> log.error("Error calling Gemini API: {}", error.getMessage()))
                    .onErrorReturn("I apologize, but I'm experiencing technical difficulties. " +
                            "Please try again in a moment or contact our support team for assistance.");

        } catch (Exception e) {
            log.error("Error generating response: {}", e.getMessage());
            return Mono.just("I'm sorry, I encountered an error while processing your request. " +
                    "Please try again or contact support.");
        }
    }

    /**
     * Create an enhanced prompt that includes RAG context
     */
    private String createRagPrompt(String userQuery, String context) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("You are AxleXpert AI, a helpful assistant for the AxleXpert vehicle service system. ");
        promptBuilder.append("Use the following context information to provide accurate and helpful responses ");
        promptBuilder.append("about our services, booking process, locations, and vehicle maintenance.\n\n");

        promptBuilder.append("CONTEXT INFORMATION:\n");
        promptBuilder.append(context);
        promptBuilder.append("\n\n");

        promptBuilder.append("INSTRUCTIONS:\n");
        promptBuilder.append("- Answer based on the context provided above\n");
        promptBuilder.append("- Be friendly, professional, and helpful\n");
        promptBuilder.append("- If asked about services not in the context, politely redirect to contact support\n");
        promptBuilder.append("- Keep responses concise but informative\n");
        promptBuilder.append("- Always encourage booking services when appropriate\n");
        promptBuilder.append("- If you don't know something, admit it and suggest contacting support\n\n");

        promptBuilder.append("USER QUESTION: ");
        promptBuilder.append(userQuery);
        promptBuilder.append("\n\nRESPONSE:");

        return promptBuilder.toString();
    }

    /**
     * Build the request body for Gemini API
     */
    private Map<String, Object> buildGeminiRequest(String prompt, RagRequest ragRequest) {
        Map<String, Object> requestBody = new HashMap<>();

        // Create contents array
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        // Add generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", ragRequest.getTemperature() != null ?
                ragRequest.getTemperature() : 0.7);
        generationConfig.put("maxOutputTokens", ragRequest.getMaxTokens() != null ?
                ragRequest.getMaxTokens() : 1000);
        generationConfig.put("topP", 0.8);
        generationConfig.put("topK", 40);

        requestBody.put("generationConfig", generationConfig);

        // Add safety settings to ensure appropriate responses
        requestBody.put("safetySettings", List.of(
                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
        ));

        return requestBody;
    }

    /**
     * Extract the generated text from Gemini API response
     */
    private String extractResponseFromGemini(String responseJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);
            JsonNode candidatesNode = rootNode.path("candidates");

            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode firstCandidate = candidatesNode.get(0);
                JsonNode contentNode = firstCandidate.path("content");
                JsonNode partsNode = contentNode.path("parts");

                if (partsNode.isArray() && partsNode.size() > 0) {
                    JsonNode firstPart = partsNode.get(0);
                    String text = firstPart.path("text").asText();

                    if (!text.isEmpty()) {
                        return text.trim();
                    }
                }
            }

            // Check for error in response
            JsonNode errorNode = rootNode.path("error");
            if (!errorNode.isMissingNode()) {
                String errorMessage = errorNode.path("message").asText();
                log.error("Gemini API error: {}", errorMessage);
                return "I'm experiencing technical difficulties. Please try again or contact support.";
            }

            log.warn("No valid response found in Gemini API response: {}", responseJson);
            return "I'm sorry, I couldn't generate a response. Please try rephrasing your question.";

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            return "I encountered an error while processing the response. Please try again.";
        }
    }

    /**
     * Generate a simple response without RAG context (for basic queries)
     */
    public Mono<String> generateSimpleResponse(String query, String sessionId) {
        RagRequest request = new RagRequest();
        request.setQuery(query);
        request.setContext("AxleXpert is a vehicle service management system.");
        request.setSessionId(sessionId);

        return generateResponse(request);
    }

    /**
     * Check if the Gemini API is configured properly
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() &&
               !apiKey.equals("${GEMINI_API_KEY}");
    }
}
