package com.login.AxleXpert.common;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //Send welcome email with login credentials to new employee
    public void sendWelcomeEmail(String toEmail, String password, String role, String branchName) {
        try {
            System.out.println("=== PREPARING EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Password: " + password);
            System.out.println("Role: " + role);
            System.out.println("Branch: " + branchName);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("axlexpert.info@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Welcome to AxleXpert - Account Created");
            
            String emailBody = createWelcomeEmailTemplate(toEmail, password, role, branchName);
            helper.setText(emailBody, true); // true = HTML content
            
            System.out.println("Sending email via JavaMailSender...");
            mailSender.send(message);
            System.out.println("✓ Welcome email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send email to " + toEmail);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }
    
    private String createWelcomeEmailTemplate(String email, String password, String role, String branchName) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Welcome to AxleXpert</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 30px 20px;">
                            <table role="presentation" style="max-width: 600px; width: 100%%; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background-color: #3498db; padding: 30px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 26px; font-weight: 600;">
                                            AxleXpert
                                        </h1>
                                        <p style="margin: 8px 0 0 0; color: #ecf0f1; font-size: 14px;">
                                            Automotive Service Management
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 35px 30px;">
                                        <h2 style="margin: 0 0 20px 0; color: #2c3e50; font-size: 20px; font-weight: 600;">
                                            Welcome to AxleXpert
                                        </h2>
                                        
                                        <p style="margin: 0 0 15px 0; color: #555555; font-size: 15px; line-height: 1.6;">
                                            Dear Employee,
                                        </p>
                                        
                                        <p style="margin: 0 0 20px 0; color: #555555; font-size: 15px; line-height: 1.6;">
                                            Your account has been successfully created. You have been assigned as <strong>%s</strong> at the <strong>%s</strong> branch.
                                        </p>
                                        
                                        <!-- Credentials -->
                                        <table style="width: 100%%; border-collapse: collapse; margin: 25px 0; background-color: #f8f9fa; border-left: 3px solid #3498db;">
                                            <tr>
                                                <td style="padding: 20px;">
                                                    <p style="margin: 0 0 15px 0; color: #2c3e50; font-size: 16px; font-weight: 600;">
                                                        Login Credentials
                                                    </p>
                                                    <table style="width: 100%%; border-collapse: collapse;">
                                                        <tr>
                                                            <td style="padding: 8px 0; color: #7f8c8d; font-size: 14px; width: 100px;">
                                                                Email:
                                                            </td>
                                                            <td style="padding: 8px 0; color: #2c3e50; font-size: 14px; font-weight: 500;">
                                                                %s
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 8px 0; color: #7f8c8d; font-size: 14px;">
                                                                Password:
                                                            </td>
                                                            <td style="padding: 8px 0;">
                                                                <span style="background-color: #ecf0f1; color: #c0392b; padding: 6px 12px; border-radius: 3px; font-size: 15px; font-weight: 600; font-family: 'Courier New', monospace; letter-spacing: 1px;">%s</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Security Notice -->
                                        <div style="background-color: #fff3cd; border-left: 3px solid #ffc107; padding: 15px; margin: 20px 0;">
                                            <p style="margin: 0; color: #856404; font-size: 14px; line-height: 1.6;">
                                                <strong>Important:</strong> Please change your password immediately after your first login for security purposes.
                                            </p>
                                        </div>
                                        
                                        <p style="margin: 20px 0 0 0; color: #555555; font-size: 14px; line-height: 1.6;">
                                            If you have any questions or need assistance, please contact your system administrator.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 25px 30px; border-top: 1px solid #e0e0e0;">
                                        <p style="margin: 0; color: #555555; font-size: 14px; text-align: center;">
                                            Best regards,<br>
                                            <strong style="color: #3498db;">AxleXpert Team</strong>
                                        </p>
                                        <p style="margin: 12px 0 0 0; color: #999999; font-size: 12px; text-align: center;">
                                            This is an automated message. Please do not reply to this email.
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, role, branchName, email, password);
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
