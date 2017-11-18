package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient
public class TicketEndpointService extends Service {

    private QName ticketEndpointPort;

    public TicketEndpointService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
        ticketEndpointPort = new QName(serviceName.getNamespaceURI(),
                "TicketEndpointPort");
    }

    @WebEndpoint(name = "TicketEndpointPort")
    public TicketEndpoint getTicketEndpointPort() {
        return super.getPort(ticketEndpointPort, TicketEndpoint.class);
    }

    @WebEndpoint(name = "TicketEndpointPort")
    public TicketEndpoint getTicketEndpointPort(WebServiceFeature... features) {
        return super.getPort(ticketEndpointPort, TicketEndpoint.class, features);
    }

}
