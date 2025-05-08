package com.InterviewSimulator.util;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class HibernateUtil {

    // Static instance of Hibernate's SessionFactory (singleton)
    private static final SessionFactory sessionFactory;

    // Sender email credentials
    private static final String FROM_EMAIL = "tosendnotification111@gmail.com"; // Replace with your email
    private static final String PASSWORD = "Xyz@12345"; // ⚠️ Use Gmail App Password, not your main password

    /**
     * Sends an email using Jakarta Mail (formerly JavaMail API).
     * 
     * @param toEmail the recipient's email address
     * @param subject the subject of the email
     * @param body    the content/body of the email
     */
    public static void sendEmail(String toEmail, String subject, String body) {
        // Set up SMTP properties for Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
        props.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP host
        props.put("mail.smtp.port", "587"); // Gmail SMTP port

        // Create a session with authentication
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            // Create a MIME email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL)); // Sender's email
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail) // Recipient's email
            );
            message.setSubject(subject); // Subject line
            message.setText(body);       // Message content

            // Send the email
            Transport.send(message);
            System.out.println("✅ Email sent to: " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace(); // Print any errors if sending fails
        }
    }

    // Static block to initialize the SessionFactory only once
    static {
        try {
            // Build the SessionFactory from hibernate.cfg.xml configuration
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            // Log the error if the SessionFactory creation fails
            System.err.println("❌ Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex); // Terminate if factory setup fails
        }
    }

    /**
     * Provides access to the Hibernate SessionFactory instance.
     * 
     * @return a singleton instance of SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
