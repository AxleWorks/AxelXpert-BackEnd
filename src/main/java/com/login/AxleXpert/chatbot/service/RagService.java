package com.login.AxleXpert.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG (Retrieval-Augmented Generation) Service
 * Handles knowledge base processing, text chunking, and similarity search
 * for retrieving relevant context to enhance chatbot responses
 */
@Service
@Slf4j
public class RagService {

    @Value("${chatbot.rag.chunk-size:1000}")
    private int chunkSize;

    @Value("${chatbot.rag.chunk-overlap:200}")
    private int chunkOverlap;

    private List<String> knowledgeChunks;
    private Map<String, Map<String, Integer>> chunkVectors;

    /**
     * Initialize the RAG service by loading and processing the knowledge base
     */
    @PostConstruct
    public void initialize() {
        log.info("Initializing RAG Service...");
        loadAndProcessKnowledgeBase();
        log.info("RAG Service initialized with {} knowledge chunks", knowledgeChunks.size());
    }

    /**
     * Load knowledge base from file and split into chunks
     */
    private void loadAndProcessKnowledgeBase() {
        try {
            ClassPathResource resource = new ClassPathResource("rag-knowledge-base.txt");
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String fullText = content.toString();
            knowledgeChunks = createTextChunks(fullText);
            chunkVectors = createVectorizedChunks(knowledgeChunks);

        } catch (IOException e) {
            log.error("Error loading knowledge base: {}", e.getMessage());
            // Initialize with empty lists to prevent null pointer exceptions
            knowledgeChunks = new ArrayList<>();
            chunkVectors = new HashMap<>();
        }
    }

    /**
     * Split text into overlapping chunks for better context retrieval
     */
    private List<String> createTextChunks(String text) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder currentChunk = new StringBuilder();
        int currentLength = 0;

        for (String sentence : sentences) {
            if (currentLength + sentence.length() > chunkSize && currentLength > 0) {
                chunks.add(currentChunk.toString().trim());

                // Create overlap by keeping the last part of the current chunk
                String overlap = getOverlapText(currentChunk.toString());
                currentChunk = new StringBuilder(overlap);
                currentLength = overlap.length();
            }

            currentChunk.append(sentence).append(" ");
            currentLength += sentence.length() + 1;
        }

        // Add the last chunk if it's not empty
        if (currentLength > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * Create overlap text from the end of a chunk
     */
    private String getOverlapText(String text) {
        if (text.length() <= chunkOverlap) {
            return text;
        }
        return text.substring(text.length() - chunkOverlap);
    }

    /**
     * Create TF-IDF like vectors for each chunk (simplified version)
     */
    private Map<String, Map<String, Integer>> createVectorizedChunks(List<String> chunks) {
        Map<String, Map<String, Integer>> vectors = new HashMap<>();

        for (int i = 0; i < chunks.size(); i++) {
            String chunkId = "chunk_" + i;
            Map<String, Integer> wordFreq = createWordFrequencyMap(chunks.get(i));
            vectors.put(chunkId, wordFreq);
        }

        return vectors;
    }

    /**
     * Create word frequency map for a text chunk
     */
    private Map<String, Integer> createWordFrequencyMap(String text) {
        Map<String, Integer> wordFreq = new HashMap<>();

        // Convert to lowercase and split into words
        String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .split("\\s+");

        for (String word : words) {
            if (word.length() > 2) { // Filter out very short words
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        return wordFreq;
    }

    /**
     * Calculate cosine similarity between two word frequency maps
     */
    private double calculateCosineSimilarity(Map<String, Integer> vectorA, Map<String, Integer> vectorB) {
        Set<String> intersection = new HashSet<>(vectorA.keySet());
        intersection.retainAll(vectorB.keySet());

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String key : intersection) {
            dotProduct += vectorA.get(key) * vectorB.get(key);
        }

        for (int value : vectorA.values()) {
            normA += Math.pow(value, 2);
        }

        for (int value : vectorB.values()) {
            normB += Math.pow(value, 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * Retrieve most relevant context chunks for a given query
     */
    public String retrieveRelevantContext(String query, int maxChunks) {
        if (knowledgeChunks.isEmpty()) {
            return "No knowledge base available. Please contact support for assistance.";
        }

        Map<String, Integer> queryVector = createWordFrequencyMap(query);
        List<ChunkScore> scores = new ArrayList<>();

        // Calculate similarity scores for each chunk
        for (Map.Entry<String, Map<String, Integer>> entry : chunkVectors.entrySet()) {
            String chunkId = entry.getKey();
            Map<String, Integer> chunkVector = entry.getValue();

            double similarity = calculateCosineSimilarity(queryVector, chunkVector);
            int chunkIndex = Integer.parseInt(chunkId.split("_")[1]);

            scores.add(new ChunkScore(chunkIndex, similarity));
        }

        // Sort by similarity score and get top chunks
        List<String> topChunks = scores.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(maxChunks)
                .map(cs -> knowledgeChunks.get(cs.chunkIndex))
                .collect(Collectors.toList());

        return String.join("\n\n", topChunks);
    }

    /**
     * Retrieve relevant context with default number of chunks
     */
    public String retrieveRelevantContext(String query) {
        return retrieveRelevantContext(query, 3); // Default to top 3 most relevant chunks
    }

    /**
     * Simple search in knowledge base (keyword-based)
     */
    public List<String> searchKeywords(String query) {
        String lowerQuery = query.toLowerCase();
        return knowledgeChunks.stream()
                .filter(chunk -> chunk.toLowerCase().contains(lowerQuery))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Get total number of knowledge chunks
     */
    public int getKnowledgeBaseSize() {
        return knowledgeChunks.size();
    }

    /**
     * Helper class to store chunk similarity scores
     */
    private static class ChunkScore {
        final int chunkIndex;
        final double score;

        ChunkScore(int chunkIndex, double score) {
            this.chunkIndex = chunkIndex;
            this.score = score;
        }
    }
}
