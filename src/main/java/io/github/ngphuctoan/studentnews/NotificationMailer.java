package io.github.ngphuctoan.studentnews;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class NotificationMailer {
    private final Session session;

    public NotificationMailer() {
        Properties props = new Properties();
        props.put("mail.smtp.host", System.getenv("SMTP_HOST"));
        props.put("mail.smtp.port", System.getenv("SMTP_PORT"));
        props.put("mail.smtp.auth", System.getenv("SMTP_AUTH"));
        props.put("mail.smtp.starttls.enable", System.getenv("SMTP_STARTTLS_ENABLE"));

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(System.getenv("SMTP_USER_NAME"), System.getenv("SMTP_PASSWORD"));
            }
        });
    }

    public void sendMail(Notification notification) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(System.getenv("MAIL_FROM")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(System.getenv("MAIL_TO")));
        msg.setSubject(notification.title());
        String details = notification.details();
        String content = details != null && details.isBlank() ? details : notification.summary();
        msg.setContent(content, "text/html; charset=utf-8");
        Transport.send(msg);
    }
}
