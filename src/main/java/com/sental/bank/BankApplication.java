package com.sental.bank;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.concurrent.CountDownLatch;

@Configuration
@ComponentScan("com.sental.bank")
@EnableJpaRepositories("com.sental.bank.repository")
@EnableTransactionManagement
@EnableKafka
@EnableScheduling
public class BankApplication {
    public static void main(String[] args) throws InterruptedException {
        var context = new AnnotationConfigApplicationContext(BankApplication.class);
        context.registerShutdownHook();
        new CountDownLatch(1).await();
    }
}
