package com.sental.bank.service;

import com.sental.bank.model.*;
import com.sental.bank.repository.AccountRepository;
import com.sental.bank.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferProcessor {
    private final AccountRepository accounts;
    private final TransferRepository transfers;

    public TransferProcessor(AccountRepository accounts, TransferRepository transfers) {
        this.accounts = accounts; this.transfers = transfers;
    }

    @Transactional
    public void process(TransferMessage message) {
        if (transfers.existsById(message.id())) return;
        if (message.id() == null || message.amount() == null || message.amount().signum() <= 0 ||
                message.fromAccountId() == null || message.toAccountId() == null ||
                message.fromAccountId().equals(message.toAccountId())) {
            throw new InvalidTransferException("Некорректные данные перевода");
        }

        long firstId = Math.min(message.fromAccountId(), message.toAccountId());
        long secondId = Math.max(message.fromAccountId(), message.toAccountId());
        Account firstAccount = findAccountForUpdate(firstId);
        Account secondAccount = findAccountForUpdate(secondId);

        Account from = firstAccount.getId().equals(message.fromAccountId()) ? firstAccount : secondAccount;
        Account to = from == firstAccount ? secondAccount : firstAccount;
        if (from.getBalance().compareTo(message.amount()) < 0) {
            throw new InvalidTransferException("Недостаточно средств на счёте " + from.getId());
        }
        from.setBalance(from.getBalance().subtract(message.amount()));
        to.setBalance(to.getBalance().add(message.amount()));
        transfers.save(new TransferRecord(message.id(), from, to, message.amount(), TransferStatus.DONE));
    }

    private Account findAccountForUpdate(long id) {
        return accounts.findByIdForUpdate(id).orElseThrow(() -> new InvalidTransferException("Счёт не найден: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(TransferMessage message) {
        if (message.id() == null || transfers.existsById(message.id())) return;
        Account from = accounts.findById(message.fromAccountId()).orElseThrow();
        Account to = accounts.findById(message.toAccountId()).orElseThrow();
        transfers.save(new TransferRecord(message.id(), from, to, message.amount(), TransferStatus.FAILED));
    }
}
