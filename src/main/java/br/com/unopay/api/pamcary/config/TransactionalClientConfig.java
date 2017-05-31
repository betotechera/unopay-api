package br.com.unopay.api.pamcary.config;

import br.com.unopay.api.pamcary.transactional.WSTransacional_Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class TransactionalClientConfig {

        /*@Bean
        public Jaxb2Marshaller marshaller() {
            final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setContextPath("com.prototype.wsdl");
            return marshaller;
        }*/

        @Bean
        public WSTransacional_Service wSTransacional_Service() {
            final WSTransacional_Service client = new WSTransacional_Service();
           /* client.setDefaultUri("http://webservice.pamcard.jee.pamcary.com.br");
            client.setMarshaller(marshaller);
            client.setUnmarshaller(marshaller);*/
            return client;
        }
}
