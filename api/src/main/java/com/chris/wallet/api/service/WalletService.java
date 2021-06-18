package com.chris.wallet.api.service;

import com.chris.wallet.api.dto.PlayerApi;
import com.chris.wallet.api.dto.PlayerBalanceApi;
import com.chris.wallet.api.dto.TransactionApi;
import com.chris.wallet.api.dto.TransactionHistoryResponseApi;
import com.chris.wallet.api.model.Transaction;

import java.util.UUID;

public interface WalletService {

    TransactionApi addTransaction(TransactionApi transactionApi);

    PlayerBalanceApi getBalance(UUID playerId);

    TransactionHistoryResponseApi getPlayerTransactionHistory(UUID playerId);


}
