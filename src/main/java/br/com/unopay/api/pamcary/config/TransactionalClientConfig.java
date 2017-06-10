package br.com.unopay.api.pamcary.config;

import br.com.unopay.api.pamcary.transactional.WSTransacional_Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class TransactionalClientConfig {

        @Bean
        public WSTransacional_Service wSTransacional_Service() {
            final WSTransacional_Service client = new WSTransacional_Service();
            return client;
        }
}
