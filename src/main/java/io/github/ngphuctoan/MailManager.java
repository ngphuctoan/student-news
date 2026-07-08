package io.github.ngphuctoan;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailManager {
    private final Session session;

    public MailManager() {
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

    public void sendNewsMail(News news) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(System.getenv("MAIL_FROM")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(System.getenv("MAIL_TO")));
        msg.setSubject(news.title());
        msg.setText("""
                %s
                
                Chi tiết: https://studentnews.tdtu.edu.vn/ThongBao/Detail/%s
                """.formatted(news.summary(), news.id()));
        Transport.send(msg);
    }
}
