package com.login.AxleXpert.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send HTML email (for password reset, etc.)
     */
    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML content
            helper.setFrom("axlexpert.info@gmail.com");
            
            mailSender.send(mimeMessage);
            System.out.println("✓ Email sent successfully to: " + to);
            
        } catch (MessagingException e) {
            System.err.println("✗ Failed to send email to " + to);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send welcome email with login credentials to new employee
     */
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

    /**
     * Generate a random 6-character password
     */
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
