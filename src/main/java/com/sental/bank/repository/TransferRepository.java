package com.sental.bank.repository;

import com.sental.bank.model.TransferRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferRecord, UUID> {}
