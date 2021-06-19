package com.chris.wallet.api.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionApi implements Serializable {

    private static final long serialVersionUID = -3711275764983640943L;
    @NotNull
    private UUID id;

    @NotNull
    private BigDecimal amount;

    private Currency currency;

    @NotNull
    private PaymentDirection paymentDirection;

    private LocalDateTime transactionTime;

    @NotBlank
    private UUID playerId;

}
