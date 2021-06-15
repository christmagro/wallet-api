package com.chris.wallet.api;

import com.chris.wallet.api.dao.PlayerDao;
import com.chris.wallet.api.dao.impl.PlayerDaoImpl;
import com.chris.wallet.api.exception.PlayerNotFoundException;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.repository.PlayerRepository;
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

import java.util.UUID;

import static org.junit.Assert.assertThrows;

@DataJpaTest
@RunWith(JUnitParamsRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class JpaPlayerTest {

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


    @Before
    public void before() {
        transactionTemplate.execute(status -> testEntityManager.getEntityManager().createQuery("DELETE FROM Transaction transaction").executeUpdate());
        transactionTemplate.execute(status -> testEntityManager.getEntityManager().createQuery("DELETE FROM Player player").executeUpdate());

    }

    @Test
    @Transactional
    public void createAndSave_shouldCreatePlayerAsExpectedAndPersist() {
        final Player saved = playerDao.addPlayer(Player.builder().name("chris").surname("magro").username("christmagro@gmail.com").build());
        Assertions.assertThat(testEntityManager.find(Player.class, saved.getId())).isEqualTo(saved);
    }

    @Test
    public void createAndSaveUsernameAlreadyExists_shouldFailFromCreatingAndThrowException() {
        playerDao.addPlayer(Player.builder().name("chris").surname("magro").username("christmagro@gmail.com").build());
        assertThrows(DataIntegrityViolationException.class, () -> {
            playerDao.addPlayer(Player.builder().name("chris").surname("magro").username("christmagro@gmail.com").build());
        });
    }

    @Test
    public void createAndSaveNameMissing_shouldFailFromCreatingAndThrowException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            playerDao.addPlayer(Player.builder().surname("magro").username("christmagro@gmail.com").build());
        });
    }

    @Test
    public void createAndSaveSurnameMissing_shouldFailFromCreatingAndThrowException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            playerDao.addPlayer(Player.builder().name("chris").username("christmagro@gmail.com").build());
        });
    }

    @Test
    public void createAndSaveUsernameMissing_shouldFailFromCreatingAndThrowException() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            playerDao.addPlayer(Player.builder().name("chris").surname("magro").build());
        });
    }

    @Test
    @Transactional
    public void getPlayer_should_return_player_by_id_successfully() {
        final Player saved = playerDao.addPlayer(Player.builder().name("chris").surname("magro").username("christmagro@gmail.com").build());
        final Player returnedPlayer = playerDao.getPlayer(saved.getId());
        Assertions.assertThat(testEntityManager.find(Player.class, saved.getId())).isEqualTo(returnedPlayer);
    }


    @Test
    public void getInvalidPlayer_should_return_player_by_id_successfully() {
        assertThrows(PlayerNotFoundException.class, () -> playerDao.getPlayer(UUID.randomUUID()));
    }


    @TestConfiguration
    public static class JpaSessionTestConfiguration {

        @Bean
        public PlayerDaoImpl PlayerDao(final PlayerRepository repository) {
            return new PlayerDaoImpl(repository);
        }
    }

}
