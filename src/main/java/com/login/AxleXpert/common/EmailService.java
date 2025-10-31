package com.login.AxleXpert.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
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
            
            String subject = "Welcome to AxleXpert - Your Account Details";
            String htmlBody = buildWelcomeEmail(toEmail, password, role, branchName);
            
            sendEmail(toEmail, subject, htmlBody);
            
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Build HTML email template for welcome email
     */
    private String buildWelcomeEmail(String email, String password, String role, String branchName) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }" +
                ".content { background-color: #f9f9f9; padding: 20px; }" +
                ".credentials { background-color: white; padding: 15px; border-left: 4px solid #1976d2; margin: 20px 0; }" +
                ".footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>AxleXpert</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<h2>Welcome to AxleXpert!</h2>" +
                "<p>Hello,</p>" +
                "<p>Your account has been successfully created as a " + role.toLowerCase() + " at " + branchName + " branch. Here are your login credentials:</p>" +
                "<div class='credentials'>" +
                "<p style='margin: 5px 0;'><strong>Email:</strong> " + email + "</p>" +
                "<p style='margin: 5px 0;'><strong>Password:</strong> " + password + "</p>" +
                "</div>" +
                "<p>Please login and change your password as soon as possible for security reasons.</p>" +
                "<p>For security reasons, keep your credentials secure and don't share them with anyone.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2025 AxleXpert. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
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
