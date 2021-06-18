package com.chris.wallet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponseApi implements Serializable {
    private static final long serialVersionUID = -8921738010979654134L;

    private List<TransactionApi> transactions;
}
