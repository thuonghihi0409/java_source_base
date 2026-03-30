package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.from:noreply@localhost}")
    private String fromAddress;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendVerificationEmail(String toEmail, String verifyUrl) {
        String subject = "Xac thuc email - He thong tim viec";
        String body = "Chao ban,\n\nVui long nhan vao lien ket de xac thuc email:\n" + verifyUrl
                + "\n\nNeu ban khong dang ky, bo qua email nay.\n";
        send(toEmail, subject, body, "VERIFY", verifyUrl);
    }

    public void sendPasswordResetEmail(String toEmail, String resetHintUrl) {
        String subject = "Dat lai mat khau - He thong tim viec";
        String body = "Chao ban,\n\nDe dat lai mat khau, mo lien ket sau tren trang web cua ung dung:\n"
                + resetHintUrl
                + "\n\nSau do nhap mat khau moi va gui yeu cau den API POST /api/auth/reset-password.\n"
                + "Lien ket het han sau mot thoi gian ngan.\n";
        send(toEmail, subject, body, "RESET", resetHintUrl);
    }

    private void send(String to, String subject, String body, String kind, String devLogUrl) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.warn("[Email chua cau hinh SMTP] {} -> to={} | {}", kind, to, devLogUrl);
            log.debug("Noi dung email:\n{}", body);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            sender.send(msg);
        } catch (Exception e) {
            log.error("Gui email that bai: {}", e.getMessage());
            log.warn("[Fallback log] {} | {}", kind, devLogUrl);
        }
    }
}
