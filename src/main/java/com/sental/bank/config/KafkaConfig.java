package com.sental.bank.config;

import com.sental.bank.model.TransferMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.json.JsonMapper;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Bean
    ProducerFactory<String, TransferMessage> producerFactory(@Value("${spring.kafka.bootstrap-servers}") String brokers) throws Exception {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
        DefaultKafkaProducerFactory<String, TransferMessage> factory = new DefaultKafkaProducerFactory<>(props);
        factory.setTransactionIdPrefix("bank-producer-");
        return factory;
    }

    @Bean KafkaTemplate<String, TransferMessage> kafkaTemplate(ProducerFactory<String, TransferMessage> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    ConsumerFactory<String, TransferMessage> consumerFactory(@Value("${spring.kafka.bootstrap-servers}") String brokers, JsonMapper mapper) {
        JacksonJsonDeserializer<TransferMessage> valueDeserializer = new JacksonJsonDeserializer<>(TransferMessage.class, mapper);
        valueDeserializer.addTrustedPackages("com.sental.bank.model");
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bank-transfer-consumers");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 512);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 2000);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransferMessage> kafkaListenerContainerFactory(ConsumerFactory<String, TransferMessage> cf) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, TransferMessage>();
        factory.setConsumerFactory(cf);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }
}
