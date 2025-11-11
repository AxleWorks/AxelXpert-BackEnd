package com.login.AxleXpert.notifications.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.login.AxleXpert.notifications.entity.FirebaseToken;
import com.login.AxleXpert.notifications.repository.FirebaseTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    private final FirebaseTokenRepository firebaseTokenRepository;

    public String sendNotificationToToken(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent notification to token: {}", token);
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to token {}: {}", token, e.getMessage());
            
            // If token is invalid, remove it from database
            if (isInvalidTokenError(e)) {
                removeInvalidToken(token);
            }
            
            throw new RuntimeException("Failed to send FCM notification", e);
        }
    }

    @Transactional
    public List<String> sendNotificationToUser(Long userId, String title, String body) {
        List<FirebaseToken> tokens = firebaseTokenRepository.findByUserId(userId);
        List<String> responses = new ArrayList<>();

        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for user ID: {}", userId);
            return responses;
        }

        for (FirebaseToken tokenEntity : tokens) {
            try {
                String response = sendNotificationToToken(tokenEntity.getToken(), title, body);
                responses.add(response);
            } catch (Exception e) {
                log.error("Failed to send notification to user {} on token {}", userId, tokenEntity.getToken());
            }
        }

        log.info("Sent {} notifications to user {} out of {} devices", responses.size(), userId, tokens.size());
        return responses;
    }

    public void sendNotificationToMultipleUsers(List<Long> userIds, String title, String body) {
        for (Long userId : userIds) {
            try {
                sendNotificationToUser(userId, title, body);
            } catch (Exception e) {
                log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
            }
        }
    }

    private boolean isInvalidTokenError(FirebaseMessagingException e) {
        String errorCode = e.getErrorCode().toString();
        return "registration-token-not-registered".equals(errorCode) || 
               "invalid-registration-token".equals(errorCode);
    }

    @Transactional
    private void removeInvalidToken(String token) {
        try {
            firebaseTokenRepository.deleteByToken(token);
            log.info("Removed invalid FCM token from database");
        } catch (Exception e) {
            log.error("Failed to remove invalid token: {}", e.getMessage());
        }
    }
}
