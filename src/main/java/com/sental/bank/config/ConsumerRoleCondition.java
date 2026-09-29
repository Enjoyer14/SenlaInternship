package com.sental.bank.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

// Kafka слушатель в экземплярах выполняющих роль потребителя (app.role = consumer)
// producer по умолчанию
public class ConsumerRoleCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "consumer".equals(context.getEnvironment().getProperty("app.role", "producer"));
    }
}
