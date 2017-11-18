package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "CobrancaEndpoint")
@XmlSeeAlso({ObjectFactory.class})
public interface CobrancaEndpoint {

    @WebMethod
    @Action(input = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/getVersionRequest", output = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/getVersionResponse")
    @RequestWrapper(localName = "getVersion", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.GetVersion")
    @ResponseWrapper(localName = "getVersionResponse", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.GetVersionResponse")
    @WebResult(name = "return", targetNamespace = "")
    java.lang.String getVersion();

    @WebMethod
    @Action(input = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/consultaTituloRequest", output = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/consultaTituloResponse", fault = {@FaultAction(className = WebServiceException.class, value = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/consultaTitulo/Fault/WebServiceException")})
    @RequestWrapper(localName = "consultaTitulo", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.ConsultaTitulo")
    @ResponseWrapper(localName = "consultaTituloResponse", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.ConsultaTituloResponse")
    @WebResult(name = "return", targetNamespace = "")
    TituloGenericResponse consultaTitulo(
        @WebParam(name = "dto", targetNamespace = "")
        TituloGenericRequest dto
    ) throws WebServiceException;

    @WebMethod
    @Action(input = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/registraTituloRequest", output = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/registraTituloResponse", fault = {@FaultAction(className = WebServiceException.class, value = "http://impl.webservice.ymb.app.bsbr.altec.com/CobrancaEndpoint/registraTitulo/Fault/WebServiceException")})
    @RequestWrapper(localName = "registraTitulo", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.RegistraTitulo")
    @ResponseWrapper(localName = "registraTituloResponse", targetNamespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", className = "br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb.RegistraTituloResponse")
    @WebResult(name = "return", targetNamespace = "")
    TituloGenericResponse registraTitulo(
        @WebParam(name = "dto", targetNamespace = "")
        TituloGenericRequest dto
    ) throws WebServiceException;
}
