package com.chris.wallet.api.dao.impl;

import com.chris.wallet.api.dao.PlayerDao;
import com.chris.wallet.api.exception.PlayerNotFoundException;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlayerDaoImpl implements PlayerDao {

    private final PlayerRepository playerRepository;


    @Override
    public Player getPlayer(UUID id) {
        return playerRepository.findById(id)
                               .orElseThrow(PlayerNotFoundException::new);
    }

    @Override
    public Player addPlayer(final Player player) {
        return playerRepository.save(player);
    }
}
