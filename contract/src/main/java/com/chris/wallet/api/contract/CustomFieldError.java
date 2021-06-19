package com.chris.wallet.api.contract;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomFieldError {
    private static final long serialVersionUID = 115431264722216393L;
    private String field;
    private String code;
    private Object rejectedValue;
}

