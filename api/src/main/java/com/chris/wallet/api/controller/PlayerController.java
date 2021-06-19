package com.chris.wallet.api.controller;

import com.chris.wallet.api.dao.impl.PlayerDaoImpl;
import com.chris.wallet.api.model.Player;
import com.chris.wallet.api.model.Transaction;
import com.chris.wallet.api.model.type.TransactionType;
import com.chris.wallet.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class PlayerController {

    private final WalletService walletService;
    private final PlayerDaoImpl playerDao;
    private final CurrencyConverter currencyConverter;


    @PostMapping()
    public TransactionApi post() {
        final Player player = playerDao.addPlayer(Player.builder().name("chris").surname("magro").username("christmagro" + RandomString.make(5) + "@gmail.com").build());


        walletService.addTransaction(TransactionApi.builder()
                                                   .id(UUID.randomUUID())
                                                   .paymentDirection(PaymentDirection.CREDIT)
                                                   .amount(BigDecimal.valueOf(600.00))
                                                   .currency(currencyConverter.convertToEntityAttribute("EUR"))
                                                   .playerId(player.getId())
                                                   .build());

        final TransactionApi eur = walletService.addTransaction(TransactionApi.builder()
                                                                              .id(UUID.randomUUID())
                                                                              .paymentDirection(PaymentDirection.DEBIT)
                                                                              .amount(BigDecimal.valueOf(400.00))
                                                                              .currency(currencyConverter.convertToEntityAttribute("EUR"))
                                                                              .playerId(player.getId())
                                                                              .build());
        return eur;
    }

    @GetMapping()
    public PlayerBalanceApi getBalance(@RequestParam final UUID playerId) {
        return walletService.getBalance(playerId);
    }


    @GetMapping(path = "/history")
    public TransactionHistoryResponseApi getTransactionHistory(@RequestParam final UUID playerId) {
        return walletService.getPlayerTransactionHistory(playerId);
    }

//    @GetMapping()
//    private void test() {
//        final ResponseEntity<ExchangRateResponse> exchangeRate = exchangeRateApi.getExchangeRate("0e6b215c947d4cd0a4e669fe718cb80b");
//        System.out.println("t");
//    }
}
