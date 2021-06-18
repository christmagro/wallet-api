package com.chris.wallet.api.converter;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.util.Currency;

@Component
public class CurrencyConverter implements AttributeConverter<Currency, String> {
    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return currency.getCurrencyCode();
    }

    @Override
    public Currency convertToEntityAttribute(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
