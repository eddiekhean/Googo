package com.example.notification.service;

import com.example.notification.factory.MailStrategyFactory;
import com.example.notification.model.MailType;
import com.example.notification.strategy.MailStrategy;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final MailStrategyFactory factory;

    public MailService(JavaMailSender mailSender, MailStrategyFactory factory) {
        this.mailSender = mailSender;
        this.factory = factory;
    }

    public void send(String to, MailType type, Object data) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);

            MailStrategy strategy = factory.getStrategy(type);
            strategy.prepareMail(helper, data);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send mail of type " + type, e);
        }
    }
}

