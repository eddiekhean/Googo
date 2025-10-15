package com.example.notification.strategy;
import org.springframework.mail.javamail.MimeMessageHelper;
public interface MailStrategy {
    void prepareMail(MimeMessageHelper helper, Object context) throws Exception;
}
