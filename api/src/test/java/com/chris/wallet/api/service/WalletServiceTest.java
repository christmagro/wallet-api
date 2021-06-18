package com.chris.wallet.api.service;

import com.chris.wallet.api.converter.CurrencyConverter;
import com.chris.wallet.api.dao.TransactionDao;
import com.chris.wallet.api.dto.PaymentDirection;
import com.chris.wallet.api.dto.PlayerBalanceApi;
import com.chris.wallet.api.dto.TransactionApi;
import com.chris.wallet.api.dto.TransactionHistoryResponseApi;
import com.chris.wallet.api.exception.InvalidExchangeRate;
import com.chris.wallet.api.exception.NotEnoughFundsException;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.model.Transaction;
import com.chris.wallet.api.model.type.TransactionType;
import com.chris.wallet.api.service.impl.WalletServiceImpl;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceTest {

    @Mock
    private TransactionDao transactionDao;

    @Mock
    private CurrencyConverter currencyConverter;

    private WalletService underTest;

    @Mock
    private MapperFacade mapper;

    @Mock
    private RateExchangeService rateExchangeService;

    private final static BigDecimal ENOUGH_FUNDS = BigDecimal.valueOf(5.75);
    private final static BigDecimal NOT_ENOUGH_FUNDS = BigDecimal.valueOf(20.50);
    private final static BigDecimal EUR_EXCHANGE_RATE = BigDecimal.valueOf(0.833324);
    private final static BigDecimal USD_EXCHANGE_RATE = BigDecimal.valueOf(1);
    private final static String EURO_CURRENCY = "EUR";
    private final static String USD_CURRENCY = "USD";

    @Before
    public void init() {
        underTest = new WalletServiceImpl(transactionDao, rateExchangeService, currencyConverter, mapper);
    }

    @Test(expected = NotEnoughFundsException.class)
    public void createDebitRequest_with_not_enough_funds() {
        //given
        val transactions = getTransactions();
        val transactionApi = getDebitTransactionApi(NOT_ENOUGH_FUNDS);
        val playerApi = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(playerApi.getId())).thenReturn(transactions);
        when(rateExchangeService.getExchangeRate(EURO_CURRENCY)).thenReturn(Optional.of(EUR_EXCHANGE_RATE));
        //then
        underTest.addTransaction(transactionApi);

    }


    @Test
    public void createDebitRequest_with_enough_funds() {
        //given
        val transactions = getTransactions();
        val transactionApi = getDebitTransactionApi(ENOUGH_FUNDS);
        val playerApi = getPlayer();
        //when
        when(mapper.map(transactionApi, Transaction.class)).thenReturn(getDebitTransaction(ENOUGH_FUNDS));
        when(mapper.map(getDebitTransaction(ENOUGH_FUNDS), TransactionApi.class)).thenReturn(transactionApi);
        when(transactionDao.getAllPlayerTransactions(playerApi.getId())).thenReturn(transactions);
        when(transactionDao.addTransaction(getDebitTransaction(ENOUGH_FUNDS))).thenReturn(getDebitTransaction(ENOUGH_FUNDS));
        when(rateExchangeService.getExchangeRate(EURO_CURRENCY)).thenReturn(Optional.of(BigDecimal.valueOf(0.833324)));
        //then
        underTest.addTransaction(transactionApi);
        verify(transactionDao, times(1)).getAllPlayerTransactions(playerApi.getId());
        verify(transactionDao, times(1)).addTransaction(getDebitTransaction(ENOUGH_FUNDS));
        verify(mapper, times(1)).map(getDebitTransactionApi(ENOUGH_FUNDS), Transaction.class);
        verify(transactionDao, times(1)).addTransaction(getDebitTransaction(ENOUGH_FUNDS));
        verify(mapper, times(1)).map(getDebitTransaction(ENOUGH_FUNDS), TransactionApi.class);

    }

    @Test
    public void get_correct_balance_amount_for_non_base_currency() {
        //given
        val transactions = getTransactions();
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        when(rateExchangeService.getExchangeRate(EURO_CURRENCY)).thenReturn(Optional.of(EUR_EXCHANGE_RATE));
        //then
        final PlayerBalanceApi balance = underTest.getBalance(player.getId());
        verify(rateExchangeService, times(2)).getExchangeRate(EURO_CURRENCY);
        Assert.assertEquals(PlayerBalanceApi.builder().amount(BigDecimal.valueOf(10.8001).setScale(2, RoundingMode.HALF_DOWN)).currency(currencyConverter.convertToEntityAttribute("USD")).build(), balance);
    }

    @Test
    public void get_correct_balance_amount_for_base_currency() {
        //given
        val transactions = getUSDTransactions();
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        when(rateExchangeService.getExchangeRate(USD_CURRENCY)).thenReturn(Optional.of(USD_EXCHANGE_RATE));
        //then
        final PlayerBalanceApi balance = underTest.getBalance(player.getId());
        verify(rateExchangeService, times(2)).getExchangeRate(USD_CURRENCY);
        Assert.assertEquals(PlayerBalanceApi.builder().amount(BigDecimal.valueOf(35).setScale(2, RoundingMode.HALF_DOWN)).currency(currencyConverter.convertToEntityAttribute("USD")).build(), balance);
    }

    @Test
    public void get_correct_balance_amount_for_mixed_transaction_currency() {
        //given
        val transactions = getMixedTransactions();
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        when(rateExchangeService.getExchangeRate(EURO_CURRENCY)).thenReturn(Optional.of(EUR_EXCHANGE_RATE));
        when(rateExchangeService.getExchangeRate(USD_CURRENCY)).thenReturn(Optional.of(USD_EXCHANGE_RATE));
        //then
        final PlayerBalanceApi balance = underTest.getBalance(player.getId());
        verify(rateExchangeService, times(2)).getExchangeRate(USD_CURRENCY);
        verify(rateExchangeService, times(2)).getExchangeRate(EURO_CURRENCY);
        Assert.assertEquals(PlayerBalanceApi.builder().amount(BigDecimal.valueOf(45.8001).setScale(2, RoundingMode.HALF_DOWN)).currency(currencyConverter.convertToEntityAttribute("USD")).build(), balance);
    }

    @Test(expected = InvalidExchangeRate.class)
    public void not_supported_currency_should_throw_invalid_exchange_rate() {
        //given
        val transactions = getTransactions();
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        //then
        underTest.getBalance(player.getId());
    }

    @Test
    public void get_transactions_for_player_successfully() {
        //given
        val transactions = getMixedTransactions();
        val transactionApis = getMixedTransactionsApi();
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        when(mapper.mapAsList(transactions, TransactionApi.class)).thenReturn(transactionApis);
        //then
        final TransactionHistoryResponseApi playerTransactionHistory = underTest.getPlayerTransactionHistory(player.getId());
        assertThat(playerTransactionHistory.getTransactions(), hasSize(4));
        assertThat(playerTransactionHistory.getTransactions().get(0), equalTo(transactionApis.get(3)));
        assertThat(playerTransactionHistory.getTransactions().get(3), equalTo(transactionApis.get(0)));
    }

    //Total Balance Amount 9 EUR  -> USD 10.8001
    public List<Transaction> getTransactions() {
        return Stream.of(Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(10))
                                    .currency(EURO_CURRENCY)
                                    .amount(BigDecimal.ONE)
                                    .transactionType(TransactionType.DEBIT)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(1))
                                    .currency(EURO_CURRENCY)
                                    .transactionType(TransactionType.CREDIT)
                                    .amount(BigDecimal.TEN)
                                    .player(getPlayer())
                                    .build()
                        ).collect(Collectors.toList());
    }

    //Total Balance Amount 35 USD
    public List<Transaction> getUSDTransactions() {
        return Stream.of(Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(10))
                                    .currency(USD_CURRENCY)
                                    .amount(BigDecimal.valueOf(25.00))
                                    .transactionType(TransactionType.CREDIT)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(1))
                                    .currency(USD_CURRENCY)
                                    .transactionType(TransactionType.CREDIT)
                                    .amount(BigDecimal.TEN)
                                    .player(getPlayer())
                                    .build()
                        ).collect(Collectors.toList());
    }

    //Total Balance Amount 45.8001 USD
    public List<Transaction> getMixedTransactions() {
        return Stream.of(Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(10))
                                    .currency(USD_CURRENCY)
                                    .amount(BigDecimal.valueOf(25.00))
                                    .transactionType(TransactionType.CREDIT)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(1))
                                    .currency(USD_CURRENCY)
                                    .transactionType(TransactionType.CREDIT)
                                    .amount(BigDecimal.TEN)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(10))
                                    .currency(EURO_CURRENCY)
                                    .amount(BigDecimal.ONE)
                                    .transactionType(TransactionType.DEBIT)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(1))
                                    .currency(EURO_CURRENCY)
                                    .transactionType(TransactionType.CREDIT)
                                    .amount(BigDecimal.TEN)
                                    .player(getPlayer())
                                    .build()
                        ).collect(Collectors.toList());
    }

    //Total Balance Amount 45.8001 USD
    public List<TransactionApi> getMixedTransactionsApi() {
        return Stream.of(TransactionApi.builder()
                                       .id(UUID.randomUUID())
                                       .transactionTime(LocalDateTime.now().minusHours(4))
                                       .currency(currencyConverter.convertToEntityAttribute(USD_CURRENCY))
                                       .paymentDirection(PaymentDirection.CREDIT)
                                       .amount(BigDecimal.valueOf(25.00))
                                       .build(),
                         TransactionApi.builder()
                                       .id(UUID.randomUUID())
                                       .transactionTime(LocalDateTime.now().minusHours(3))
                                       .currency(currencyConverter.convertToEntityAttribute(USD_CURRENCY))
                                       .paymentDirection(PaymentDirection.CREDIT)
                                       .amount(BigDecimal.TEN)
                                       .build(),
                         TransactionApi.builder()
                                       .id(UUID.randomUUID())
                                       .transactionTime(LocalDateTime.now().minusHours(2))
                                       .currency(currencyConverter.convertToEntityAttribute(EURO_CURRENCY))
                                       .amount(BigDecimal.ONE)
                                       .paymentDirection(PaymentDirection.DEBIT)
                                       .build(),
                         TransactionApi.builder()
                                       .id(UUID.randomUUID())
                                       .transactionTime(LocalDateTime.now().minusHours(1))
                                       .currency(currencyConverter.convertToEntityAttribute(EURO_CURRENCY))
                                       .paymentDirection(PaymentDirection.CREDIT)
                                       .amount(BigDecimal.TEN)
                                       .build()
                        ).collect(Collectors.toList());
    }

    public Player getPlayer() {
        return Player.builder()
                     .id(UUID.fromString("3d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                     .name("Test")
                     .surname("Player")
                     .username("test@test.com")
                     .build();
    }

    public TransactionApi getDebitTransactionApi(BigDecimal amount) {
        return TransactionApi.builder()
                             .id(UUID.fromString("4d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                             .paymentDirection(PaymentDirection.DEBIT)
                             .amount(amount)
                             .currency(Currency.getInstance(Locale.getDefault()))
                             .playerId(getPlayer().getId())
                             .build();
    }

    public Transaction getDebitTransaction(BigDecimal amount) {
        return Transaction.builder()
                          .id(UUID.fromString("4d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                          .transactionType(TransactionType.DEBIT)
                          .amount(amount)
                          .currency(EURO_CURRENCY)
                          .player(getPlayer())
                          .build();
    }

}
