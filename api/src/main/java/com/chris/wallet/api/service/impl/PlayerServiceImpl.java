package com.chris.wallet.api.service.impl;

import com.chris.wallet.api.contract.PlayerApi;
import com.chris.wallet.api.contract.PlayerRequestApi;
import com.chris.wallet.api.contract.PlayersApi;
import com.chris.wallet.api.dao.PlayerDao;
import com.chris.wallet.api.exception.UsernameAlreadyExistsException;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerDao playerDao;

    private final MapperFacade mapperFacade;

    @Override
    public PlayerApi createPlayer(PlayerRequestApi playerRequestApi) {
        try {
            return mapperFacade.map(playerDao.addPlayer(mapperFacade.map(playerRequestApi, Player.class)), PlayerApi.class);
        } catch (DataIntegrityViolationException dve) {
            throw new UsernameAlreadyExistsException(playerRequestApi.getUsername());
        }
    }

    @Override
    public PlayerApi updatePlayer(UUID playerId, PlayerRequestApi playerRequestApi) {
            return mapperFacade.map(playerDao.editPlayer(playerId, mapperFacade.map(playerRequestApi, Player.class)), PlayerApi.class);
    }

    @Override
    public PlayerApi getPlayer(UUID playerId) {
        return mapperFacade.map(playerDao.getPlayer(playerId), PlayerApi.class);
    }

    @Override
    public PlayersApi getPlayers() {
        return PlayersApi.builder()
                         .players(mapperFacade.mapAsList(playerDao.getAllPlayers(), PlayerApi.class))
                         .build();
    }
}
