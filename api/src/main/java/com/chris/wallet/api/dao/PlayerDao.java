package com.chris.wallet.api.dao;

import com.chris.wallet.api.model.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerDao {

    Player getPlayer(final UUID id);

    Player addPlayer(final Player player);

    Player editPlayer(final UUID playerId, final Player player);

    List<Player> getAllPlayers();
}
