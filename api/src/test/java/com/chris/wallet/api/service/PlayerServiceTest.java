package com.chris.wallet.api.service;

import com.chris.wallet.api.contract.PlayerApi;
import com.chris.wallet.api.contract.PlayerRequestApi;
import com.chris.wallet.api.contract.PlayersApi;
import com.chris.wallet.api.dao.PlayerDao;
import com.chris.wallet.api.exception.PlayerNotFoundException;
import com.chris.wallet.api.exception.UsernameAlreadyExistsException;
import com.chris.wallet.api.mapper.BaseConfigurableMapper;
import com.chris.wallet.api.mapper.PlayerMapperConfigurer;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.service.impl.PlayerServiceImpl;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerServiceTest {

    @Mock
    private PlayerDao playerDao;

    @Mock
    private MapperFacade mapper;

    @InjectMocks
    private PlayerServiceImpl underTest;


    @Before
    public void init() {
        mapper = new BaseConfigurableMapper(Collections.singletonList(new PlayerMapperConfigurer()));
        underTest = new PlayerServiceImpl(playerDao, mapper);
    }

    @Test
    public void create_player_should_be_created_successfully() {
        //given
        val playerApiRequest = getPlayerApiRequest();
        val playerApi = getPlayerApiResponse1();
        //when
        when(playerDao.addPlayer(addPlayer())).thenReturn(getPlayer1());
        //then
        final PlayerApi response = underTest.createPlayer(playerApiRequest);
        verify(playerDao, times(1)).addPlayer(addPlayer());
        assertEquals(playerApi, response);
    }

    @Test(expected = UsernameAlreadyExistsException.class)
    public void create_already_exist_username_should_fail() {
        //given
        val playerApiRequest = getPlayerApiRequest();
        //when
        when(playerDao.addPlayer(addPlayer())).thenThrow(new DataIntegrityViolationException(""));
        //then
        underTest.createPlayer(playerApiRequest);
    }


    @Test
    public void update_player_should_be_updated_successfully() {
        //given
        val playerApiRequest = getPlayerApiRequest();
        val playerApi = getPlayerApiResponse1();
        val player = addPlayer();
        //when
        when(playerDao.editPlayer(playerApi.getId(), player)).thenReturn(getPlayer1());
        //then
        final PlayerApi response = underTest.updatePlayer(playerApi.getId(), playerApiRequest);
        verify(playerDao, times(1)).editPlayer(playerApi.getId(), player);
        assertEquals(playerApi, response);
    }

    @Test(expected = PlayerNotFoundException.class)
    public void update_player_username_incorrect_should_fail() {
        //given
        val playerApiRequest = getPlayerApiRequest();
        val playerApi = getPlayerApiResponse1();
        val player = addPlayer();
        //when
        when(playerDao.editPlayer(playerApi.getId(), player)).thenThrow(new PlayerNotFoundException());
        //then
        underTest.updatePlayer(playerApi.getId(), playerApiRequest);
    }


    @Test
    public void get_player_should_be_returned_successfully() {
        //given
        val playerApi = getPlayerApiResponse1();
        //when
        when(playerDao.getPlayer(playerApi.getId())).thenReturn(getPlayer1());
        //then
        final PlayerApi response = underTest.getPlayer(playerApi.getId());
        verify(playerDao, times(1)).getPlayer(playerApi.getId());
        assertEquals(playerApi, response);
    }

    @Test(expected = PlayerNotFoundException.class)
    public void get_invalid_player_should_fail() {
        //given
        val playerApi = getPlayerApiResponse1();
        //when
        when(playerDao.getPlayer(playerApi.getId())).thenThrow(new PlayerNotFoundException());
        //then
        underTest.getPlayer(playerApi.getId());
        verify(playerDao, times(1)).getPlayer(playerApi.getId());
    }

    @Test
    public void get_all_players_should_be_returned_successfully() {
        //given
        val playersApi = getPlayersApi();
        val players = getPlayers();
        //when
        when(playerDao.getAllPlayers()).thenReturn(players);
        //then
        final PlayersApi response = underTest.getPlayers();
        verify(playerDao, times(1)).getAllPlayers();
        assertThat(response.getPlayers(), hasSize(2));
        assertEquals(playersApi, response);
    }


    public Player addPlayer() {
        return Player.builder()
                     .name("Test")
                     .surname("Player")
                     .username("test@test.com")
                     .build();
    }

    public Player getPlayer1() {
        return Player.builder()
                     .id(UUID.fromString("1d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                     .name("Test")
                     .surname("Player")
                     .username("test@test.com")
                     .build();
    }

    public Player getPlayer2() {
        return Player.builder()
                     .id(UUID.fromString("2d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                     .name("Test2")
                     .surname("Player2")
                     .username("test2@test.com")
                     .build();
    }

    public List<Player> getPlayers() {
        return Stream.of(getPlayer1(), getPlayer2()).collect(Collectors.toList());
    }

    public PlayerRequestApi getPlayerApiRequest() {
        return PlayerRequestApi.builder()
                               .name("Test")
                               .surname("Player")
                               .username("test@test.com")
                               .build();
    }

    public PlayersApi getPlayersApi() {
        return PlayersApi.builder()
                         .players(Stream.of(getPlayerApiResponse1(), getPlayerApiResponse2()).collect(Collectors.toList()))
                         .build();
    }

    public PlayerApi getPlayerApiResponse1() {
        return PlayerApi.builder()
                        .id(UUID.fromString("1d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                        .name("Test")
                        .surname("Player")
                        .username("test@test.com")
                        .build();
    }

    public PlayerApi getPlayerApiResponse2() {
        return PlayerApi.builder()
                        .id(UUID.fromString("2d55c4c5-7c6e-4d40-9cba-15ae5253c6ee"))
                        .name("Test2")
                        .surname("Player2")
                        .username("test2@test.com")
                        .build();
    }

}
