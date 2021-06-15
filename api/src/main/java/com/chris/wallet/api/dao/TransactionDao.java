package com.chris.wallet.api.dao;

import com.chris.wallet.api.model.Transaction;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface TransactionDao {


    List<Transaction> getAllPlayerTransactions(final UUID playerId);

    Transaction addTransaction(final Transaction transaction);

    Transaction getTransaction(final UUID transactionId);

}
