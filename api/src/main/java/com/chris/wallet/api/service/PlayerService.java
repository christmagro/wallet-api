package com.chris.wallet.api.service;



import com.chris.wallet.api.contract.PlayerApi;
import com.chris.wallet.api.contract.PlayerRequestApi;
import com.chris.wallet.api.contract.PlayersApi;

import java.util.UUID;

public interface PlayerService {

    PlayerApi createPlayer(final PlayerRequestApi playerRequestApi);

    PlayerApi updatePlayer(final UUID playerId, final PlayerRequestApi playerRequestApi);

    PlayerApi getPlayer(final UUID playerId);

    PlayersApi getPlayers();

}
