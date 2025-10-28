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
            helper.setSubject("🎉 Welcome to AxleXpert - Your Account is Ready!");
            
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
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7fa;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 20px;">
                            <table role="presentation" style="max-width: 600px; width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); overflow: hidden;">
                                
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 30px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: 700; letter-spacing: -0.5px;">
                                            🚗 AxleXpert
                                        </h1>
                                        <p style="margin: 10px 0 0 0; color: #e0e7ff; font-size: 16px; font-weight: 400;">
                                            Your Automotive Service Management Platform
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Welcome Message -->
                                <tr>
                                    <td style="padding: 40px 30px 30px 30px;">
                                        <h2 style="margin: 0 0 20px 0; color: #1a202c; font-size: 24px; font-weight: 600;">
                                            Welcome Aboard! 🎉
                                        </h2>
                                        <p style="margin: 0 0 20px 0; color: #4a5568; font-size: 16px; line-height: 1.6;">
                                            We're thrilled to have you join the <strong>AxleXpert</strong> team! Your account has been successfully created and you're all set to start managing automotive services with ease.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Credentials Box -->
                                <tr>
                                    <td style="padding: 0 30px 30px 30px;">
                                        <div style="background-color: #f7fafc; border-left: 4px solid #667eea; border-radius: 8px; padding: 25px;">
                                            <h3 style="margin: 0 0 20px 0; color: #2d3748; font-size: 18px; font-weight: 600;">
                                                🔐 Your Login Credentials
                                            </h3>
                                            <table style="width: 100%%; border-collapse: collapse;">
                                                <tr>
                                                    <td style="padding: 8px 0; color: #718096; font-size: 14px; font-weight: 600; width: 120px;">
                                                        Email:
                                                    </td>
                                                    <td style="padding: 8px 0; color: #2d3748; font-size: 14px; font-weight: 500;">
                                                        %s
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 8px 0; color: #718096; font-size: 14px; font-weight: 600;">
                                                        Password:
                                                    </td>
                                                    <td style="padding: 8px 0;">
                                                        <code style="background-color: #edf2f7; color: #e53e3e; padding: 6px 12px; border-radius: 4px; font-size: 15px; font-weight: 600; letter-spacing: 1px;">
                                                            %s
                                                        </code>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 8px 0; color: #718096; font-size: 14px; font-weight: 600;">
                                                        Role:
                                                    </td>
                                                    <td style="padding: 8px 0;">
                                                        <span style="background-color: #c3dafe; color: #2c5282; padding: 4px 12px; border-radius: 12px; font-size: 13px; font-weight: 600; text-transform: uppercase;">
                                                            %s
                                                        </span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 8px 0; color: #718096; font-size: 14px; font-weight: 600;">
                                                        Branch:
                                                    </td>
                                                    <td style="padding: 8px 0; color: #2d3748; font-size: 14px; font-weight: 500;">
                                                        📍 %s
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Security Notice -->
                                <tr>
                                    <td style="padding: 0 30px 30px 30px;">
                                        <div style="background-color: #fff5f5; border-left: 4px solid #f56565; border-radius: 8px; padding: 20px;">
                                            <p style="margin: 0; color: #742a2a; font-size: 14px; line-height: 1.6;">
                                                <strong>⚠️ Important Security Notice:</strong><br>
                                                Please change your password immediately after your first login to ensure your account security. Keep your credentials confidential and never share them with anyone.
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Next Steps -->
                                <tr>
                                    <td style="padding: 0 30px 30px 30px;">
                                        <h3 style="margin: 0 0 15px 0; color: #2d3748; font-size: 18px; font-weight: 600;">
                                            🚀 Next Steps
                                        </h3>
                                        <ul style="margin: 0; padding-left: 20px; color: #4a5568; font-size: 15px; line-height: 1.8;">
                                            <li>Login to your AxleXpert account</li>
                                            <li>Update your password in settings</li>
                                            <li>Complete your profile information</li>
                                            <li>Explore the dashboard and features</li>
                                        </ul>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f7fafc; padding: 30px; text-align: center; border-top: 1px solid #e2e8f0;">
                                        <p style="margin: 0 0 10px 0; color: #4a5568; font-size: 15px; font-weight: 500;">
                                            Best regards,<br>
                                            <strong style="color: #667eea;">The AxleXpert Team</strong>
                                        </p>
                                        <p style="margin: 15px 0 0 0; color: #a0aec0; font-size: 12px;">
                                            This is an automated message. Please do not reply to this email.<br>
                                            If you need assistance, contact your system administrator.
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, email, password, role, branchName);
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
