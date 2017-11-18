package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient
public class CobrancaEndpointService extends Service {

    private QName cobrancaEndpointPort;

    public CobrancaEndpointService(URL wsdlLocation, QName service) {
        super(wsdlLocation, service);
        this.cobrancaEndpointPort = new QName(service.getNamespaceURI(),"CobrancaEndpointPort");
    }

    @WebEndpoint(name = "CobrancaEndpointPort")
    public CobrancaEndpoint getCobrancaEndpointPort() {
        return super.getPort(cobrancaEndpointPort, CobrancaEndpoint.class);
    }

    @WebEndpoint(name = "CobrancaEndpointPort")
    public CobrancaEndpoint getCobrancaEndpointPort(WebServiceFeature... features) {
        return super.getPort(cobrancaEndpointPort, CobrancaEndpoint.class, features);
    }

}
