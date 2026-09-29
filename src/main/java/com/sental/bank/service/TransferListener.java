package com.sental.bank.service;

import com.sental.bank.model.TransferMessage;
import com.sental.bank.config.ConsumerRoleCondition;
import com.sental.bank.exception.InvalidTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Conditional(ConsumerRoleCondition.class)
public class TransferListener {
    private static final Logger log = LoggerFactory.getLogger(TransferListener.class);
    private final TransferProcessor processor;

    public TransferListener(TransferProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "${app.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<TransferMessage> batch) {
        log.info("Получен батч переводов: {} шт.", batch.size());
        for (TransferMessage message : batch) {
            log.info("Начата обработка перевода {}", message.id());
            try {
                processor.process(message);
                log.info("Перевод {} обработан успешно (или уже был сохранён ранее)", message.id());
            } catch (InvalidTransferException invalid) {
                log.error("Перевод {} не прошёл проверку: {}", message.id(), invalid.getMessage());
            } catch (Exception failure) {
                log.error("Ошибка транзакции при обработке перевода {}", message.id(), failure);
                try {
                    processor.recordFailure(message);
                } catch (Exception auditFailure) {
                    log.error("Не удалось сохранить запись об ошибочном переводе {}", message.id(), auditFailure);
                    throw new IllegalStateException("Не удалось сохранить запись об ошибочном переводе", auditFailure);
                }
            }
        }
    }
}
