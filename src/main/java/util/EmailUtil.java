package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtil {
    private static final String SMTP_HOST = getConfig("HRM_SMTP_HOST", "hrm.smtp.host", "smtp.gmail.com");
    private static final String SMTP_PORT = getConfig("HRM_SMTP_PORT", "hrm.smtp.port", "587");
    private static final String FROM_EMAIL = getConfig("HRM_MAIL_USERNAME", "hrm.mail.username", "");
    private static final String APP_PASSWORD = getConfig("HRM_MAIL_PASSWORD", "hrm.mail.password", "");

    public static boolean sendResetPasswordEmail(String toEmail, String newPassword) {
        if (FROM_EMAIL.isBlank() || APP_PASSWORD.isBlank()) {
            System.out.println("Mail is not configured. New password for " + toEmail + ": " + newPassword);
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("HRM Password Reset");
            message.setText(
                    "Your HRM password has been reset by admin.\n\n" +
                            "New password: " + newPassword + "\n\n" +
                            "Please login and change your password immediately."
            );

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String getConfig(String envName, String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);

        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv(envName);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }
}
