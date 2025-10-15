package com.example.notification.strategy.Impl;


import com.example.notification.strategy.MailStrategy;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;



@Component("WELCOME")
public class WelcomeMailStrategy implements MailStrategy {

    private final TemplateEngine emailTemplateEngine;

    public WelcomeMailStrategy(TemplateEngine emailTemplateEngine) {
        this.emailTemplateEngine = emailTemplateEngine;
    }

    @Override
    public void prepareMail(MimeMessageHelper helper, Object data) throws Exception {
        Map<String, Object> ctxMap = (Map<String, Object>) data;

        Context ctx = new Context();
        ctx.setVariables(ctxMap);

        String html = emailTemplateEngine.process("welcome", ctx);

        helper.setSubject("Welcome to GoGoFood!");
        helper.setText(html, true);
    }
}
