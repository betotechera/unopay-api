package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://impl.webservice.dl.app.bsbr.altec.com/", name = "TicketEndpoint")
@XmlSeeAlso({ObjectFactory.class})
public interface TicketEndpoint {

    @WebMethod
    @Action(input = "http://impl.webservice.dl.app.bsbr.altec.com/TicketEndpoint/validateRequest", output = "http://impl.webservice.dl.app.bsbr.altec.com/TicketEndpoint/validateResponse")
    @RequestWrapper(localName = "validate", targetNamespace = "http://impl.webservice.dl.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.Validate")
    @ResponseWrapper(localName = "validateResponse", targetNamespace = "http://impl.webservice.dl.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.ValidateResponse")
    @WebResult(name = "ValidateTicketResponse", targetNamespace = "")
    ValidateTicketResponse validate(
            @WebParam(name = "ValidateTicketRequest", targetNamespace = "")
                    ValidateTicketRequest validateTicketRequest
    );

    @WebMethod
    @Action(input = "http://impl.webservice.dl.app.bsbr.altec.com/TicketEndpoint/createRequest", output = "http://impl.webservice.dl.app.bsbr.altec.com/TicketEndpoint/createResponse")
    @RequestWrapper(localName = "create", targetNamespace = "http://impl.webservice.dl.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.Create")
    @ResponseWrapper(localName = "createResponse", targetNamespace = "http://impl.webservice.dl.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.CreateResponse")
    @WebResult(name = "TicketResponse", targetNamespace = "")
    TicketResponse create(
            @WebParam(name = "TicketRequest", targetNamespace = "")
                    TicketRequest ticketRequest
    );
}
