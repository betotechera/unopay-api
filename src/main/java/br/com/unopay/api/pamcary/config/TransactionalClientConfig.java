package br.com.unopay.api.pamcary.config;

import br.com.unopay.api.pamcary.transactional.WSTransacionalService;
import javax.xml.namespace.QName;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class TransactionalClientConfig {

    @Value("${soap.client.wsdl-location:}")
    private Resource wsdl;

    @Value("${soap.client.transacional-service:}")
    private String qname;

        @Bean
        @SneakyThrows
        public WSTransacionalService wSTransacionalService() {
            return new WSTransacionalService(wsdl.getURL(), createQname());
        }

        private QName createQname(){
            return new QName(qname, "WSTransacional");
        }
}
