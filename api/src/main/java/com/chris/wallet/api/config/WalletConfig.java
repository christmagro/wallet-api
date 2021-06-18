package com.chris.wallet.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class WalletConfig {

    @Value("${wallet.openexchange.app.id:0e6b215c947d4cd0a4e669fe718cb80b}")
    private String appId;
}
