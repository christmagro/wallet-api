package com.chris.wallet.api.dao;

import com.chris.wallet.api.model.Player;

import java.util.UUID;

public interface PlayerDao {

    Player getPlayer(final UUID id);

    Player addPlayer(final Player player);
}
