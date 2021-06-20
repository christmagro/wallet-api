package com.chris.wallet.api.controller;

import com.chris.wallet.api.contract.PlayerApi;
import com.chris.wallet.api.contract.PlayerRequestApi;
import com.chris.wallet.api.contract.PlayersApi;
import com.chris.wallet.api.contract.WalletApiResponse;
import com.chris.wallet.api.service.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/player")
@Api(tags = "Player Controller")
public class PlayerController {

    private final PlayerService playerService;


    @ApiOperation(value = "Create a new player")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Player create successfully"),
        @ApiResponse(code = 400, message = "Error invalid data provided or provided username already exists"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletApiResponse<PlayerApi> createPlayer(@RequestBody @Valid PlayerRequestApi playerRequestApi) {
        return WalletApiResponse.build(playerService.createPlayer(playerRequestApi));
    }

    @ApiOperation(value = "Update a existing player")
    @ApiResponses({
        @ApiResponse(code = 202, message = "Player updated successfully"),
        @ApiResponse(code = 400, message = "Error invalid data provided or provided username already exists"),
    })
    @PutMapping(path = "/{playerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WalletApiResponse<PlayerApi> updatePlayer(@PathVariable(name = "playerId") final UUID playerId, @RequestBody @Valid PlayerRequestApi playerRequestApi) {
        return WalletApiResponse.build(playerService.updatePlayer(playerId, playerRequestApi));
    }


    @ApiOperation(value = "Fetch an existing Player")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Relevant player details returned successfully"),
        @ApiResponse(code = 400, message = "Error player not found in case provided playerId was invalid"),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<PlayerApi> getPlayer(@RequestParam final UUID playerId) {
        return WalletApiResponse.build(playerService.getPlayer(playerId));

    }

    @ApiOperation(value = "Fetch all system Players")
    @ApiResponses({
        @ApiResponse(code = 200, message = "All players details returned successfully"),
    })
    @GetMapping(path = "/all")
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<PlayersApi> getPlayers() {
        return WalletApiResponse.build(playerService.getPlayers());
    }
}
