package com.chris.wallet.api;

import com.chris.wallet.api.dao.PlayerDao;
import com.chris.wallet.api.dao.TransactionDao;
import com.chris.wallet.api.dao.impl.TransactionDaoImpl;
import com.chris.wallet.api.exception.TransactionAlreadyExistsException;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.model.Transaction;
import com.chris.wallet.api.model.type.TransactionType;
import com.chris.wallet.api.repository.PlayerRepository;
import com.chris.wallet.api.repository.TransactionRepository;
import com.chris.wallet.api.dao.impl.PlayerDaoImpl;
import junitparams.JUnitParamsRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertThrows;

@DataJpaTest
@RunWith(JUnitParamsRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class JpaTransactionTest {

    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private TransactionDao transactionDao;

    private Player player;


    @Before
    public void before() {
        transactionTemplate.execute(status -> testEntityManager.getEntityManager().createQuery("DELETE FROM Transaction transaction").executeUpdate());
        transactionTemplate.execute(status -> testEntityManager.getEntityManager().createQuery("DELETE FROM Player player").executeUpdate());

        player = playerDao.addPlayer(Player.builder().name("chris").surname("magro").username("christmagro@gmail.com").build());

    }


    @Test
    @Transactional
    public void addTransactions_should_be_added_successfully() {
        final UUID transactionId = UUID.randomUUID();
        final Transaction transaction = transactionDao.addTransaction(Transaction.builder()
                                                                                 .id(transactionId)
                                                                                 .transactionType(TransactionType.DEBIT)
                                                                                 .currency("EUR")
                                                                                 .amount(BigDecimal.valueOf(20.00))
                                                                                 .transactionTime(LocalDateTime.now())
                                                                                 .player(player)
                                                                                 .build());


        Assertions.assertThat(testEntityManager.find(Transaction.class, transactionId)).isEqualTo(transaction);
    }

    @Test
    public void addTransactionsWithEmptyCurrency_should_fail() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            transactionDao.addTransaction(Transaction.builder()
                                                     .id(UUID.randomUUID())
                                                     .transactionType(TransactionType.DEBIT)
                                                     .amount(BigDecimal.valueOf(20.00))
                                                     .transactionTime(LocalDateTime.now())
                                                     .player(player)
                                                     .build());
        });
    }

    @Test
    public void addTransactionsWithEmptyAmount_should_fail() {
        assertThrows(DataIntegrityViolationException.class, () ->
            transactionDao.addTransaction(Transaction.builder()
                                                     .id(UUID.randomUUID())
                                                     .transactionType(TransactionType.DEBIT)
                                                     .currency("EUR")
                                                     .transactionTime(LocalDateTime.now())
                                                     .player(player)
                                                     .build()));
    }

    @Test
    public void addTransactionsWithoutType_should_fail() {
        assertThrows(DataIntegrityViolationException.class, () ->
            transactionDao.addTransaction(Transaction.builder()
                                                     .id(UUID.randomUUID())
                                                     .currency("EUR")
                                                     .amount(BigDecimal.valueOf(20.00))
                                                     .transactionTime(LocalDateTime.now())
                                                     .player(player)
                                                     .build()));
    }

    @Test
    public void addTransactionsWithoutPlayer_should_fail() {
        assertThrows(DataIntegrityViolationException.class, () ->
            transactionDao.addTransaction(Transaction.builder()
                                                     .id(UUID.randomUUID())
                                                     .currency("EUR")
                                                     .amount(BigDecimal.valueOf(20.00))
                                                     .transactionType(TransactionType.DEBIT)
                                                     .transactionTime(LocalDateTime.now())
                                                     .build()));
    }

    @Test
    public void addTransactionsWithoutTimeStamp_should_fail() {
        assertThrows(DataIntegrityViolationException.class, () ->
            transactionDao.addTransaction(Transaction.builder()
                                                     .id(UUID.randomUUID())
                                                     .currency("EUR")
                                                     .amount(BigDecimal.valueOf(20.00))
                                                     .transactionType(TransactionType.DEBIT)
                                                     .player(player)
                                                     .build()));
    }

    @Test
    public void addTransactionsSameId_should_throw_transaction_already_exist() {
        final UUID transactionId = UUID.randomUUID();
        transactionDao.addTransaction(Transaction.builder()
                                                 .id(transactionId)
                                                 .transactionType(TransactionType.DEBIT)
                                                 .currency("EUR")
                                                 .amount(BigDecimal.valueOf(20.00))
                                                 .transactionTime(LocalDateTime.now())
                                                 .player(player)
                                                 .build());

        assertThrows(TransactionAlreadyExistsException.class, () -> transactionDao.addTransaction(Transaction.builder()
                                                                                                             .id(transactionId)
                                                                                                             .transactionType(TransactionType.DEBIT)
                                                                                                             .currency("EUR")
                                                                                                             .amount(BigDecimal.valueOf(20.00))
                                                                                                             .transactionTime(LocalDateTime.now())
                                                                                                             .player(player)
                                                                                                             .build()));

    }


    @Test
    public void getInvalidPlayer_should_return_empty_list_of_transactions() {
        Assertions.assertThat(transactionDao.getAllPlayerTransactions(UUID.randomUUID())).hasSize(0);
    }

    @Test
    @Transactional
    public void getTransactionsById_should_return_transaction_successfully() {
        final UUID transactionId = UUID.randomUUID();
        final Transaction transaction = transactionDao.addTransaction(Transaction.builder()
                                                                         .id(transactionId)
                                                                         .transactionType(TransactionType.DEBIT)
                                                                         .currency("EUR")
                                                                         .amount(BigDecimal.valueOf(20.00))
                                                                         .transactionTime(LocalDateTime.now())
                                                                         .player(player)
                                                                         .build());

        Assertions.assertThat(transactionDao.getTransaction(transactionId)).isEqualTo(transaction);
    }

    @Test
    public void getTransactionsByIdThatDoesNotExist_should_return_empty_transaction_successfully() {
        Assertions.assertThat(transactionDao.getTransaction(UUID.randomUUID())).isEqualTo(Transaction.builder().build());
    }



    @TestConfiguration
    public static class JpaSessionTestConfiguration {

        @Bean
        public PlayerDao playerdao(final PlayerRepository playerRepository) {
            return new PlayerDaoImpl(playerRepository);
        }

        @Bean
        public TransactionDao transactionDao(final TransactionRepository transactionRepository) {
            return new TransactionDaoImpl(transactionRepository);
        }
    }

}
