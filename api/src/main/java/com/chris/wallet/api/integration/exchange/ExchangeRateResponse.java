
package com.chris.wallet.api.integration.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRateResponse {

    private String base;
    private String disclaimer;
    private String license;
    private Map<String, BigDecimal> rates;
    private Long timestamp;
}
