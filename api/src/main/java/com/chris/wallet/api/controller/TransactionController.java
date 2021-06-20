package com.chris.wallet.api.controller;

import com.chris.wallet.api.contract.*;
import com.chris.wallet.api.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/wallet")
@Api(tags = "Transaction Controller")
public class TransactionController {

    private final WalletService walletService;

    private final MapperFacade mapper;

    @ApiOperation(value = "Add Transaction for a player")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Player created successfully"),
        @ApiResponse(code = 400, message = "Error invalid data provided"),
        @ApiResponse(code = 402, message = "Error not enough funds in case a debit transaction exceeds the total amount of credit for the provided player"),
        @ApiResponse(code = 400, message = "Error transaction already exists, where a provided unique transaction id was already used"),
        @ApiResponse(code = 406, message = "Error invalid Exchange Rate in case provided exchange rate is not supported"),
        @ApiResponse(code = 503, message = "Error exchange rate service currently unavailable"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletApiResponse<TransactionApi> addTransaction(@RequestBody @Valid final TransactionApiRequest transactionApiRequest) {
        return WalletApiResponse.build(walletService.addTransaction(mapper.map(transactionApiRequest, TransactionApi.class)));
    }

    @ApiOperation(value = "Get current balance for a specific player")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Player balance returned successfully"),
    })
    @GetMapping(path = "/{playerId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<PlayerBalanceApi> getBalanceForPlayer(@PathVariable(name = "playerId") final UUID playerId){
        return WalletApiResponse.build(walletService.getBalance(playerId));
    }

    @ApiOperation(value = "Get all transaction history for a specific player")
    @ApiResponses({
        @ApiResponse(code = 200, message = "All player's transactions return successfully "),
    })
    @GetMapping(path = "/{playerId}/history")
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<TransactionHistoryResponseApi> getPlayerTransactions(@PathVariable(name = "playerId") final UUID playerId){
        return WalletApiResponse.build(walletService.getPlayerTransactionHistory(playerId));
    }
}
