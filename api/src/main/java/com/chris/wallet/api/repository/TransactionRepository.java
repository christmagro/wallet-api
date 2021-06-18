package com.chris.wallet.api.repository;

import com.chris.wallet.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<List<Transaction>> findByPlayer_Id(UUID id);
}
