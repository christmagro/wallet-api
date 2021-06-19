package com.chris.wallet.api.service.impl;

import com.chris.wallet.api.contract.PaymentDirection;
import com.chris.wallet.api.contract.PlayerBalanceApi;
import com.chris.wallet.api.contract.TransactionApi;
import com.chris.wallet.api.contract.TransactionHistoryResponseApi;
import com.chris.wallet.api.converter.CurrencyConverter;
import com.chris.wallet.api.dao.TransactionDao;
import com.chris.wallet.api.exception.InvalidExchangeRateException;
import com.chris.wallet.api.exception.NotEnoughFundsException;
import com.chris.wallet.api.model.Transaction;
import com.chris.wallet.api.model.type.TransactionType;
import com.chris.wallet.api.service.RateExchangeService;
import com.chris.wallet.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final TransactionDao transactionDao;

    private final RateExchangeService rateExchangeService;

    private final CurrencyConverter currencyConverter;

    private final MapperFacade mapper;

    @Override
    public TransactionApi addTransaction(TransactionApi transactionApi) {

        if (transactionApi.getPaymentDirection().equals(PaymentDirection.DEBIT)) {
            final BigDecimal currentBalance = calculateCurrentAmount(transactionDao.getAllPlayerTransactions(transactionApi.getPlayerId()));

            if (currentBalance.subtract(transactionApi.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                throw new NotEnoughFundsException();
            }
        }
        final Transaction transaction = transactionDao.addTransaction(mapper.map(transactionApi, Transaction.class));
        return mapper.map(transaction, TransactionApi.class);
    }

    @Override
    public PlayerBalanceApi getBalance(UUID playerId) {
        return PlayerBalanceApi.builder()
                               .currency(currencyConverter.convertToEntityAttribute("USD")) //default Base currency cannot be changed since openexchange only offers change of Base Rate for the paid option
                               .amount(calculateCurrentAmount(transactionDao.getAllPlayerTransactions(playerId)))
                               .playerId(playerId)
                               .build();
    }

    @Override
    public TransactionHistoryResponseApi getPlayerTransactionHistory(UUID playerId) {
        return TransactionHistoryResponseApi.builder()
                                            .transactions(mapper.mapAsList(transactionDao.getAllPlayerTransactions(playerId),
                                                                           TransactionApi.class).stream()
                                                                .sorted(Comparator.comparing(TransactionApi::getTransactionTime).reversed())
                                                                .collect(Collectors.toList()))
                                            .build();
    }


    private BigDecimal calculateCurrentAmount(List<Transaction> transactions) {
        return transactions.stream()
                           .map(transaction -> {
                               transaction.setAmount(transaction.getTransactionType().equals(TransactionType.DEBIT) ?
                                                     transaction.getAmount().negate() :
                                                     transaction.getAmount());
                               final BigDecimal exchangeRate = rateExchangeService.getExchangeRate(transaction.getCurrency())
                                                                                  .orElseThrow(InvalidExchangeRateException::new);

                               return transaction.getAmount().divide(exchangeRate, 2, RoundingMode.HALF_DOWN);


                           }).reduce(BigDecimal.ZERO, BigDecimal::add);

    }

}
