package com.example.notification.factory;

import com.example.notification.model.MailType;
import com.example.notification.strategy.MailStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MailStrategyFactory {

    private final ApplicationContext context;

    public MailStrategyFactory(ApplicationContext context) {
        this.context = context;
    }

    public MailStrategy getStrategy(MailType type) {
        return (MailStrategy) context.getBean(type.name());
    }
}