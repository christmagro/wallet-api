package com.chris.wallet.api.controller;

import com.chris.wallet.api.contract.*;
import com.chris.wallet.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/wallet")
public class TransactionController {

    private final WalletService walletService;

    private final MapperFacade mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletApiResponse<TransactionApi> addTransaction(@RequestBody @Valid final TransactionApiRequest transactionApiRequest) {
        return WalletApiResponse.build(walletService.addTransaction(mapper.map(transactionApiRequest, TransactionApi.class)));
    }

    @GetMapping(path = "/{playerId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<PlayerBalanceApi> getBalanceForPlayer(@PathVariable(name = "playerId") final UUID playerId){
        return WalletApiResponse.build(walletService.getBalance(playerId));
    }

    @GetMapping(path = "/{playerId}/history")
    @ResponseStatus(HttpStatus.OK)
    public WalletApiResponse<TransactionHistoryResponseApi> getPlayerTransactions(@PathVariable(name = "playerId") final UUID playerId){
        return WalletApiResponse.build(walletService.getPlayerTransactionHistory(playerId));
    }
}
