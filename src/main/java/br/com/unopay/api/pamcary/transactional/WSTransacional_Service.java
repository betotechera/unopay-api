package br.com.unopay.api.pamcary.transactional;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
@WebServiceClient(name = "WSTransacional",
                  wsdlLocation =
                          "file:/home/emerson/unovation/unopay-api/src/main/resources/wsdl/WSTransacional-wsdl.xml",
                  targetNamespace = "http://webservice.pamcard.jee.pamcary.com.br") 
public class WSTransacional_Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://webservice.pamcard.jee.pamcary.com.br", "WSTransacional");
    public final static QName WSTransacional =
            new QName("http://webservice.pamcard.jee.pamcary.com.br", "WSTransacional");
    static {
        URL url = null;
        try {
            url = new
                    URL("file:/home/emerson/unovation/unopay-api/src/main/resources/wsdl/WSTransacional-wsdl.xml");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(WSTransacional_Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}",
                        "file:/home/emerson/unovation/unopay-api/src/main/resources/wsdl/WSTransacional-wsdl.xml");
        }
        WSDL_LOCATION = url;
    }

    public WSTransacional_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public WSTransacional_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSTransacional_Service() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public WSTransacional_Service(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public WSTransacional_Service(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public WSTransacional_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    

    @WebEndpoint(name = "WSTransacional")
    public WSTransacional getWSTransacional() {
        return super.getPort(WSTransacional, WSTransacional.class);
    }

    @WebEndpoint(name = "WSTransacional")
    public WSTransacional getWSTransacional(WebServiceFeature... features) {
        return super.getPort(WSTransacional, WSTransacional.class, features);
    }

}
