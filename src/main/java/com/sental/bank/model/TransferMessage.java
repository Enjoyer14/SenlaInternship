package com.sental.bank.model;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferMessage(UUID id, Long fromAccountId, Long toAccountId, BigDecimal amount) {}
