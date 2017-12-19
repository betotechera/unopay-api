package br.com.unopay.api.billing.boleto.santander.service;

import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketEndpoint;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketEndpointService;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketResponse;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.CobrancaEndpoint;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.CobrancaEndpointService;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloDto;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloGenericRequest;
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.TituloGenericResponse;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.sun.xml.ws.developer.JAXWSProperties.SSL_SOCKET_FACTORY;

@Service
public class CobrancaOnlineService {

    private static final int NSU_SIZE = 999999999;
    public static final String DD_MM_YYYY = "ddMMyyyy";
    public static final String TEST = "T";
    public static final String NSU_TEST_PREFIX = "TST";
    public static final String YMB = "YMB";
    private TicketEndpoint ticketEndpoint;
    private CobrancaEndpoint cobrancaEndpoint;

    @Value("${payment.soap.santander.cobranca-online.enviroment:}")
    private String enviroment;

    @Autowired
    public CobrancaOnlineService(TicketEndpointService ticketEndpoint,
                                 CobrancaEndpointService cobrancaEndpoint,
                                 SSLSocketFactory sslConnectionSocketFactory) {
        TicketEndpoint ticketEndpointPort = ticketEndpoint.getTicketEndpointPort();
        CobrancaEndpoint cobrancaEndpointPort = cobrancaEndpoint.getCobrancaEndpointPort();
        this.ticketEndpoint = ticketEndpointPort;
        ((BindingProvider)this.ticketEndpoint).getRequestContext().put(SSL_SOCKET_FACTORY, sslConnectionSocketFactory);
        this.cobrancaEndpoint = cobrancaEndpointPort;
    }

    @SneakyThrows
    public TituloDto getTicket(List<TicketRequest.Dados.Entry> entries, String statiion) {
        TicketRequest.Dados dados = new TicketRequest.Dados().entry(entries);
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setDados(dados);
        ticketRequest.setSistema(YMB);
        TicketResponse ticketResponse = ticketEndpoint.create(ticketRequest);
        TituloGenericRequest tituloGenericRequest = new TituloGenericRequest();
        tituloGenericRequest.setTpAmbiente(enviroment);
        tituloGenericRequest.setDtNsu(new SimpleDateFormat(DD_MM_YYYY).format(new Date()));
        tituloGenericRequest.setTicket(ticketResponse.getTicket());
        tituloGenericRequest.setNsu(getNsu());
        tituloGenericRequest.setEstacao(statiion);
        TituloGenericResponse tituloGenericResponse = cobrancaEndpoint.registraTitulo(tituloGenericRequest);
        if(!StringUtils.isEmpty(tituloGenericResponse.getDescricaoErro())){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.TICKET_REGISTRATION_ERROR
                            .withOnlyArgument(tituloGenericResponse.getDescricaoErro()));
        }
        return tituloGenericResponse.getTitulo();
    }

    private String getNsu(){
        String prefix = "";
        if(TEST.equals(enviroment)){
            prefix = NSU_TEST_PREFIX;
        }
        return String.format("%s%s",prefix,String.valueOf(new java.security.SecureRandom().nextInt(NSU_SIZE)));
    }
}
