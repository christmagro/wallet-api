package com.chris.wallet.api.model;

import com.chris.wallet.api.model.type.TransactionType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transaction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Transaction {

    @Id
    @NotNull
    @Type(type = "uuid-char")
    @Column(columnDefinition = "char(36)")
    private UUID id;

    @Digits(integer = 5, fraction = 2)
    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "transaction_timestamp", columnDefinition = "TIMESTAMP")
    private LocalDateTime transactionTime;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Player player;


}
