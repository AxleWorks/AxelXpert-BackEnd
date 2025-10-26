package com.login.AxleXpert.chatbot.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO for RAG (Retrieval-Augmented Generation) requests
 * Contains the user query and relevant context retrieved from knowledge base
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RagRequest {

    /**
     * The user's original question or query
     */
    private String query;

    /**
     * Retrieved context from the knowledge base that's relevant to the query
     */
    private String context;

    /**
     * Session ID to maintain conversation context
     */
    private String sessionId;

    /**
     * Maximum number of tokens for the response
     */
    private Integer maxTokens;

    /**
     * Temperature setting for response creativity (0.0 to 1.0)
     */
    private Double temperature;

    /**
     * Constructor with essential fields
     */
    public RagRequest(String query, String context, String sessionId) {
        this.query = query;
        this.context = context;
        this.sessionId = sessionId;
        this.maxTokens = 1000;
        this.temperature = 0.7;
    }
}
