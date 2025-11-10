package com.login.AxleXpert.notifications.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.Users.repository.UserRepository;
import com.login.AxleXpert.notifications.dto.NotificationDTO;
import com.login.AxleXpert.notifications.entity.FirebaseToken;
import com.login.AxleXpert.notifications.entity.Notification;
import com.login.AxleXpert.notifications.repository.FirebaseTokenRepository;
import com.login.AxleXpert.notifications.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FirebaseTokenRepository firebaseTokenRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    @Transactional
    public NotificationDTO saveNotification(Long userId, String title, String body, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setType(type);

        Notification saved = notificationRepository.save(notification);
        log.info("Notification saved for user {}", userId);

        return convertToDTO(saved);
    }

    @Transactional
    public NotificationDTO createAndSendNotification(Long userId, String title, String body, String type) {
        NotificationDTO notification = saveNotification(userId, title, body, type);
        try {
            fcmService.sendNotificationToUser(userId, title, body);
        } catch (Exception e) {
            log.error("Failed to send push notification to user {}: {}", userId, e.getMessage());
        }

        return notification;
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public void registerToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (firebaseTokenRepository.existsByToken(token)) {
            log.info("FCM token already registered for user {}", userId);
            return;
        }

        FirebaseToken firebaseToken = new FirebaseToken();
        firebaseToken.setUser(user);
        firebaseToken.setToken(token);

        firebaseTokenRepository.save(firebaseToken);
        log.info("FCM token registered for user {}", userId);
    }

    public List<String> getUserTokens(Long userId) {
        return firebaseTokenRepository.findByUserId(userId)
                .stream()
                .map(FirebaseToken::getToken)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        notificationRepository.delete(notification);
        log.info("Notification {} deleted", notificationId);
    }


    @Transactional
    public NotificationDTO updateNotificationReadStatus(Long notificationId, Boolean isRead) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        notification.setIsRead(isRead);
        Notification updated = notificationRepository.save(notification);
        
        log.info("Notification {} marked as {}", notificationId, isRead ? "read" : "unread");
        return convertToDTO(updated);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setTitle(notification.getTitle());
        dto.setBody(notification.getBody());
        dto.setIsRead(notification.getIsRead());
        dto.setType(notification.getType());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
