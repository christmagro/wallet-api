package com.chris.wallet.api.mapper;

import com.chris.wallet.api.mapper.config.ConfigurableMapperConfigurer;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseConfigurableMapper extends ConfigurableMapper {

    private final List<ConfigurableMapperConfigurer> configurers = new ArrayList<>();

    @Autowired
    public BaseConfigurableMapper(final List<ConfigurableMapperConfigurer> configurers) {
        super(false);
        this.configurers.addAll(configurers);
        init();
    }

    @Override
    protected void configure(final MapperFactory factory) {
        configurers.forEach(configurer -> configurer.configure(factory));
    }

}
