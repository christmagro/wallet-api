package com.chris.wallet.api.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerBalanceApi  {
    private static final long serialVersionUID = 104265030184793895L;

    private Currency currency;

    private BigDecimal amount;

}
