
package br.com.unopay.api.pamcary.transactional;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _Execute_QNAME =
            new QName("http://webservice.pamcard.jee.pamcary.com.br", "execute");
    private final static QName _ExecuteResponse_QNAME =
            new QName("http://webservice.pamcard.jee.pamcary.com.br", "executeResponse");

    public ObjectFactory() {
    }
    public Execute createExecute() {
        return new Execute();
    }

    public ExecuteResponse createExecuteResponse() {
        return new ExecuteResponse();
    }

    public RequestTO createRequestTO() {
        return new RequestTO();
    }

    public FieldTO createFieldTO() {
        return new FieldTO();
    }

    public ResponseTO createResponseTO() {
        return new ResponseTO();
    }

    @XmlElementDecl(namespace = "http://webservice.pamcard.jee.pamcary.com.br", name = "execute")
    public JAXBElement<Execute> createExecute(Execute value) {
        return new JAXBElement<Execute>(_Execute_QNAME, Execute.class, null, value);
    }

    @XmlElementDecl(namespace = "http://webservice.pamcard.jee.pamcary.com.br", name = "executeResponse")
    public JAXBElement<ExecuteResponse> createExecuteResponse(ExecuteResponse value) {
        return new JAXBElement<ExecuteResponse>(_ExecuteResponse_QNAME, ExecuteResponse.class, null, value);
    }

}
