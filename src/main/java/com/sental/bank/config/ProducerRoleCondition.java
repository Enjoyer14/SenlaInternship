package com.sental.bank.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

// Позволяет выполнять инициализацию аккаунтов и генерацию транзакций в экземплярах производителях (app.role = producer)
// producer по умолчанию
public class ProducerRoleCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "producer".equals(context.getEnvironment().getProperty("app.role", "producer"));
    }
}
