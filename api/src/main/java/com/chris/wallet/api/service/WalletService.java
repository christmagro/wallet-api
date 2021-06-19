package com.chris.wallet.api.service;


import com.chris.wallet.api.contract.PlayerBalanceApi;
import com.chris.wallet.api.contract.TransactionApi;
import com.chris.wallet.api.contract.TransactionHistoryResponseApi;

import java.util.UUID;

public interface WalletService {

    TransactionApi addTransaction(TransactionApi transactionApi);

    PlayerBalanceApi getBalance(UUID playerId);

    TransactionHistoryResponseApi getPlayerTransactionHistory(UUID playerId);


}
