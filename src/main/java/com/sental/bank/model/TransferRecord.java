package com.sental.bank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transfers", indexes = @Index(name = "idx_transfer_from", columnList = "from_account_id"))
public class TransferRecord {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransferStatus status;

    protected TransferRecord() {}

    public TransferRecord(UUID id, Account fromAccount, Account toAccount, BigDecimal amount, TransferStatus status) {
        this.id = id; this.fromAccount = fromAccount; this.toAccount = toAccount; this.amount = amount; this.status = status;
    }

    public UUID getId() {
        return id;
    }
}
