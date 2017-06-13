package br.com.unopay.api.pamcary.transactional;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://webservice.pamcard.jee.pamcary.com.br", name = "WSTransacional")
public interface WSTransacional {

    @WebMethod
    @RequestWrapper(localName = "execute", targetNamespace = "http://webservice.pamcard.jee.pamcary.com.br",
            className = "br.com.unopay.api.pamcary.transactional.Execute")
    @ResponseWrapper(localName = "executeResponse", targetNamespace = "http://webservice.pamcard.jee.pamcary.com.br",
            className = "br.com.unopay.api.pamcary.transactional.ExecuteResponse")
    @WebResult(name = "return", targetNamespace = "")
    public ResponseTO execute(
        @WebParam(name = "arg0", targetNamespace = "")
                RequestTO arg0
    );
}
