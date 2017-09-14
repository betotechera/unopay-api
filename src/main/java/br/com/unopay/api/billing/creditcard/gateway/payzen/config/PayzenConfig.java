package br.com.unopay.api.billing.creditcard.gateway.payzen.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("payment.gateway")
public class PayzenConfig {

    Map<String, String> config;

}
