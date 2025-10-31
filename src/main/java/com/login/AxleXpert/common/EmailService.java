package com.login.AxleXpert.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //Send welcome email with login credentials to new employee
    public void sendWelcomeEmail(String toEmail, String password, String role, String branchName) {
        try {
            log.info("Sending welcome email to: {}", toEmail);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("axlexpert.info@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Welcome to AxleXpert - Your Account Details");
            
            String emailBody = String.format(
                "Dear Employee,\n\n" +
                "Welcome to AxleXpert!\n\n" +
                "Your account has been successfully created. Here are your login credentials:\n\n" +
                "Email: %s\n" +
                "Password: %s\n" +
                "Role: %s\n" +
                "Branch: %s\n\n" +
                "Please login and change your password as soon as possible for security reasons.\n\n" +
                "Best regards,\n" +
                "AxleXpert Team",
                toEmail, password, role, branchName
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }

    //Generate a random 6-character password
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
