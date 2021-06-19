package com.chris.wallet.api.controller;

import com.chris.wallet.api.contract.PlayerApi;
import com.chris.wallet.api.contract.PlayerRequestApi;
import com.chris.wallet.api.contract.PlayersApi;
import com.chris.wallet.api.contract.WalletApiResponse;
import com.chris.wallet.api.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/player")
public class PlayerController {

    private final PlayerService playerService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletApiResponse<PlayerApi> createPlayer(@RequestBody @Valid PlayerRequestApi playerRequestApi) {
        return WalletApiResponse.build(playerService.createPlayer(playerRequestApi));
    }

    @PutMapping(path = "/{playerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WalletApiResponse<PlayerApi> updatePlayer(@PathVariable(name = "playerId") final UUID playerId, @RequestBody @Valid PlayerRequestApi playerRequestApi) {
        return WalletApiResponse.build(playerService.updatePlayer(playerId, playerRequestApi));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<PlayerApi> getPlayer(@RequestParam final UUID playerId) {
        return WalletApiResponse.build(playerService.getPlayer(playerId));

    }

    @GetMapping(path = "/all")
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<PlayersApi> getPlayers() {
        return WalletApiResponse.build(playerService.getPlayers());
    }
}
