package com.sental.bank.service;

import com.sental.bank.model.TransferMessage;
import com.sental.bank.config.ProducerRoleCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@Component
@Conditional(ProducerRoleCondition.class)
public class TransferGenerator {
    private static final Logger log = LoggerFactory.getLogger(TransferGenerator.class);
    private final AccountBootstrap bootstrap;
    private final KafkaTemplate<String, TransferMessage> kafka;

    @Value("${app.topic}")
    private String topic;

    public TransferGenerator(AccountBootstrap bootstrap, KafkaTemplate<String, TransferMessage> kafka) {
        this.bootstrap = bootstrap;
        this.kafka = kafka;
    }

    @Scheduled(fixedRate = 200)
    public void generate() {
        List<Long> ids = bootstrap.accountIds();
        if (ids.size() < 2) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        int fromIndex = random.nextInt(ids.size());
        int toIndex;
        do {
            toIndex = random.nextInt(ids.size());
        } while (toIndex == fromIndex);

        BigDecimal amount = BigDecimal.valueOf(random.nextLong(1, 100_001), 2);
        TransferMessage message = new TransferMessage(
                UUID.randomUUID(),
                ids.get(fromIndex),
                ids.get(toIndex),
                amount
        );

        kafka.executeInTransaction(operations -> {
            operations.send(topic, null, message);
            return null;
        });

        log.info("Отправлен перевод {}: со счёта {} на счёт {}, сумма {}", message.id(), message.fromAccountId(), message.toAccountId(), message.amount());
    }
}
