package com.chris.wallet.api.service;

import com.chris.wallet.api.contract.PaymentDirection;
import com.chris.wallet.api.contract.PlayerBalanceApi;
import com.chris.wallet.api.contract.TransactionApi;
import com.chris.wallet.api.contract.TransactionHistoryResponseApi;
import com.chris.wallet.api.converter.CurrencyConverter;
import com.chris.wallet.api.dao.TransactionDao;
import com.chris.wallet.api.dao.impl.PlayerDaoImpl;
import com.chris.wallet.api.exception.InvalidExchangeRateException;
import com.chris.wallet.api.exception.NotEnoughFundsException;
import com.chris.wallet.api.mapper.BaseConfigurableMapper;
import com.chris.wallet.api.mapper.TransactionMapperConfigurer;
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
import org.mockito.InjectMocks;
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

    @InjectMocks
    private WalletServiceImpl underTest;

    private CurrencyConverter currencyConverter;

    private MapperFacade mapper;

    @Mock
    private TransactionDao transactionDao;

    @Mock
    private PlayerDaoImpl playerDao;

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
        currencyConverter = new CurrencyConverter();
        mapper = new BaseConfigurableMapper(Collections.singletonList(new TransactionMapperConfigurer(playerDao, currencyConverter)));
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
        when(rateExchangeService.getExchangeRate(USD_CURRENCY)).thenReturn(Optional.of(USD_EXCHANGE_RATE));
        //then
        underTest.addTransaction(transactionApi);

    }


    @Test
    public void createDebitRequest_with_enough_funds() {
        //given
        val transactions = getTransactions();
        val transactionApi = getDebitTransactionApi(ENOUGH_FUNDS);
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        when(transactionDao.addTransaction(any())).thenReturn(getDebitTransaction(ENOUGH_FUNDS));
        when(rateExchangeService.getExchangeRate(EURO_CURRENCY)).thenReturn(Optional.of(EUR_EXCHANGE_RATE));
        when(rateExchangeService.getExchangeRate(USD_CURRENCY)).thenReturn(Optional.of(USD_EXCHANGE_RATE));
        when(playerDao.getPlayer(transactionApi.getPlayerId())).thenReturn(player);
        //then
        underTest.addTransaction(transactionApi);
        verify(transactionDao, times(1)).getAllPlayerTransactions(player.getId());
        verify(transactionDao, times(1)).addTransaction(any());
        verify(rateExchangeService, times(3)).getExchangeRate(any());

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
        Assert.assertEquals(PlayerBalanceApi.builder().playerId(player.getId()).amount(BigDecimal.valueOf(10.8001).setScale(2, RoundingMode.HALF_DOWN)).currency(currencyConverter.convertToEntityAttribute("USD")).build(), balance);
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
        Assert.assertEquals(PlayerBalanceApi.builder().playerId(player.getId()).amount(BigDecimal.valueOf(35).setScale(2, RoundingMode.HALF_DOWN)).currency(currencyConverter.convertToEntityAttribute("USD")).build(), balance);
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
        Assert.assertEquals(PlayerBalanceApi.builder().playerId(player.getId()).amount(BigDecimal.valueOf(45.8001).setScale(2, RoundingMode.HALF_DOWN)).currency(currencyConverter.convertToEntityAttribute("USD")).build(), balance);
    }

    @Test(expected = InvalidExchangeRateException.class)
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
        val player = getPlayer();
        //when
        when(transactionDao.getAllPlayerTransactions(player.getId())).thenReturn(transactions);
        //then
        final TransactionHistoryResponseApi playerTransactionHistory = underTest.getPlayerTransactionHistory(player.getId());
        assertThat(playerTransactionHistory.getTransactions(), hasSize(4));
        assertThat(playerTransactionHistory.getTransactions().get(0), equalTo(mapper.map(transactions.get(3), TransactionApi.class)));
        assertThat(playerTransactionHistory.getTransactions().get(3), equalTo(mapper.map(transactions.get(0), TransactionApi.class)));
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
                                    .transactionTime(LocalDateTime.now().minusHours(4))
                                    .currency(USD_CURRENCY)
                                    .amount(BigDecimal.valueOf(25.00))
                                    .transactionType(TransactionType.CREDIT)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(3))
                                    .currency(USD_CURRENCY)
                                    .transactionType(TransactionType.CREDIT)
                                    .amount(BigDecimal.TEN)
                                    .player(getPlayer())
                                    .build(),
                         Transaction.builder()
                                    .id(UUID.randomUUID())
                                    .transactionTime(LocalDateTime.now().minusHours(2))
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
