package com.chris.wallet.api.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
