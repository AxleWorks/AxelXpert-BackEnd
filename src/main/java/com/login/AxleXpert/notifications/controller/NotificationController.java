package com.login.AxleXpert.notifications.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.login.AxleXpert.common.CurrentUserUtil;
import com.login.AxleXpert.common.dto.ErrorResponse;
import com.login.AxleXpert.notifications.dto.FCMTokenRegistrationDTO;
import com.login.AxleXpert.notifications.dto.NotificationDTO;
import com.login.AxleXpert.notifications.dto.UpdateNotificationReadStatusDTO;
import com.login.AxleXpert.notifications.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserUtil currentUserUtil;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable Long userId) {
        try {
            List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching user notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch notifications: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok().body("{\"message\": \"Notification deleted successfully\"}");
        } catch (Exception e) {
            log.error("Error deleting notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete notification: " + e.getMessage()));
        }
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<?> updateReadStatus(
            @PathVariable Long notificationId,
            @Valid @RequestBody UpdateNotificationReadStatusDTO updateDTO) {
        try {
            NotificationDTO notification = notificationService.updateNotificationReadStatus(
                    notificationId,
                    updateDTO.getIsRead()
            );
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            log.error("Error updating notification read status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update notification: " + e.getMessage()));
        }
    }


    @PostMapping("/token/register")
    public ResponseEntity<?> registerToken(@Valid @RequestBody FCMTokenRegistrationDTO tokenDTO) {
        try {
            Long currentUserId = currentUserUtil.getCurrentUserId();
            notificationService.registerToken(currentUserId, tokenDTO.getToken());
            return ResponseEntity.ok().body("{\"message\": \"Token registered successfully\"}");
        } catch (Exception e) {
            log.error("Error registering FCM token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to register token: " + e.getMessage()));
        }
    }


}
