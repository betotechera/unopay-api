package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.ws.WebFault;

@WebFault(name = "WebServiceException", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/")
public class WebServiceException extends Exception {
    
    private FaultInfo webServiceException;

    public WebServiceException() {
        super();
    }
    
    public WebServiceException(String message) {
        super(message);
    }
    
    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(String message, FaultInfo webServiceException) {
        super(message);
        this.webServiceException = webServiceException;
    }

    public WebServiceException(String message, FaultInfo webServiceException, Throwable cause) {
        super(message, cause);
        this.webServiceException = webServiceException;
    }

    public FaultInfo getFaultInfo() {
        return this.webServiceException;
    }
}
