package com.chris.wallet.api.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class PlayerBalanceApi extends PlayerApi {
    private static final long serialVersionUID = 104265030184793895L;

    private Currency currency;

    private BigDecimal amount;

}
