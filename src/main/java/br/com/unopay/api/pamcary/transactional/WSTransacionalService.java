package br.com.unopay.api.pamcary.transactional;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "WSTransacional")
public class WSTransacionalService extends Service {


    public WSTransacionalService(URL WSDL_LOCATION, QName SERVICE) {
        super(WSDL_LOCATION, SERVICE);
    }

    @WebEndpoint(name = "WSTransacional")
    public WSTransacional getWSTransacional() {
        return super.getPort(super.getServiceName(), WSTransacional.class);
    }

    @WebEndpoint(name = "WSTransacional")
    public WSTransacional getWSTransacional(WebServiceFeature... features) {
        return super.getPort(super.getServiceName(), WSTransacional.class, features);
    }

}
