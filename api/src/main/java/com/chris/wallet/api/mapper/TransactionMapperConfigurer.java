package com.chris.wallet.api.mapper;


import com.chris.wallet.api.contract.PaymentDirection;
import com.chris.wallet.api.contract.TransactionApi;
import com.chris.wallet.api.converter.CurrencyConverter;
import com.chris.wallet.api.dao.PlayerDao;
import com.chris.wallet.api.mapper.config.ConfigurableMapperConfigurer;
import com.chris.wallet.api.model.Transaction;
import com.chris.wallet.api.model.type.TransactionType;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Currency;


@Component
@RequiredArgsConstructor
public class TransactionMapperConfigurer implements ConfigurableMapperConfigurer {

    private final PlayerDao playerDao;
    private final CurrencyConverter currencyConverter;

    @Override
    public void configure(final MapperFactory factory) {

        factory.registerObjectFactory(new CurrencyFactory(), Currency.class);

        factory.classMap(TransactionApi.class, Transaction.class)
               .customize(new CustomMapper<>() {
                   @Override
                   public void mapAtoB(TransactionApi transactionApi, Transaction transaction, MappingContext context) {
                       transaction.setTransactionTime(LocalDateTime.now());
                       transaction.setPlayer(playerDao.getPlayer(transactionApi.getPlayerId()));
                       transaction.setTransactionType(transactionApi.getPaymentDirection().equals(PaymentDirection.DEBIT) ? TransactionType.DEBIT : TransactionType.CREDIT);
                       transaction.setCurrency(currencyConverter.convertToDatabaseColumn(transactionApi.getCurrency()));
                   }

                   @Override
                   public void mapBtoA(Transaction transaction, TransactionApi transactionApi, MappingContext context) {
                   transactionApi.setPaymentDirection(transaction.getTransactionType().equals(TransactionType.DEBIT) ? PaymentDirection.DEBIT : PaymentDirection.CREDIT);
                   }
               }).byDefault().register();
    }

    class CurrencyFactory implements ObjectFactory<Currency>{

        @Override
        public Currency create(Object source, MappingContext mappingContext) {
            if(source instanceof String){
                return currencyConverter.convertToEntityAttribute((String) source);
            }
            return null;
        }
    }

}
