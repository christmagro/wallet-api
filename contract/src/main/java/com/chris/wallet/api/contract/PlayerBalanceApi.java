package com.chris.wallet.api.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerBalanceApi implements Serializable {

    private static final long serialVersionUID = 2872270670831798479L;

    private Currency currency;

    private BigDecimal amount;

    private UUID playerId;
}
