package com.chris.wallet.api.controller;

import com.chris.wallet.api.dao.impl.PlayerDaoImpl;
import com.chris.wallet.api.dao.impl.TransactionDaoImpl;
import com.chris.wallet.api.dto.PlayersApi;
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

}
