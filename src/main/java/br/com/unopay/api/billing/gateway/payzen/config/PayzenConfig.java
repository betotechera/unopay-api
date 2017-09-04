package br.com.unopay.api.billing.gateway.payzen.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("payment.gateway")
public class PayzenConfig {

    Map<String, String> config = new HashMap<>();

}
