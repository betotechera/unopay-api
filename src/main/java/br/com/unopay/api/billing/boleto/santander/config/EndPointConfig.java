package br.com.unopay.api.billing.boleto.santander.config;

import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketEndpointService;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.CobrancaEndpointService;
import br.com.unopay.api.infra.TempFileCreator;
import javax.xml.namespace.QName;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class EndPointConfig {

    @Value("${payment.soap.santander.cobranca-online.dlb-wsdl-location:}")
    private Resource dlbwsdl;

    @Value("${payment.soap.santander.cobranca-online.ymb-wsdl-location:}")
    private Resource ymbwsdl;

    @Value("${payment.soap.santander.cobranca-online.dlb-transacional-service:}")
    private String dlbqname;

    @Value("${payment.soap.santander.cobranca-online.ymb-transacional-service:}")
    private String ymbqname;

    @Autowired
    private TempFileCreator tempFileCreator;

    @Bean
    @SneakyThrows
    public TicketEndpointService ticketEndpointService() {
        return new TicketEndpointService(tempFileCreator.createTempFile(dlbwsdl).toURL(), createDlbQname());
    }

    @Bean
    @SneakyThrows
    public CobrancaEndpointService cobrancaEndpointService() {
        return new CobrancaEndpointService(tempFileCreator.createTempFile(ymbwsdl).toURL(), createYmbQname());
    }

    private QName createDlbQname(){
        return new QName(dlbqname, "TicketEndpointService");
    }

    private QName createYmbQname(){
            return new QName(ymbqname, "CobrancaEndpointService");
        }
}
