package com.chris.wallet.api.dao.impl;

import com.chris.wallet.api.dao.TransactionDao;
import com.chris.wallet.api.model.Transaction;
import com.chris.wallet.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionDaoImpl implements TransactionDao {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getAllPlayerTransactions(UUID playerId) {
        return transactionRepository.findByPlayer_Id(playerId).orElse(Collections.emptyList());
    }

    @Override
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElse(Transaction.builder().build());
    }
}
