package com.sental.bank.service;

import com.sental.bank.model.Account;
import com.sental.bank.repository.AccountRepository;
import com.sental.bank.config.ProducerRoleCondition;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
@Conditional(ProducerRoleCondition.class)
public class AccountBootstrap {
    private final AccountRepository accounts;
    private final Map<Long, BigDecimal> accountCache = new HashMap<>();

    public AccountBootstrap(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        if (accounts.count() == 0) {
            for (int i = 0; i < 1000; i++) {
                accounts.save(new Account(new BigDecimal("10000.00")));
            }
        }
        accounts.findAll().forEach(account -> accountCache.put(account.getId(), account.getBalance()));
    }

    public List<Long> accountIds() {
        return List.copyOf(accountCache.keySet());
    }
}
