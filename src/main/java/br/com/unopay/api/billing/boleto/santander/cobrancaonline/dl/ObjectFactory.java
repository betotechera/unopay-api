package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _Create_QNAME = new QName("http://impl.webservice.dl.app.bsbr.altec.com/", "create");
    private final static QName _CreateResponse_QNAME = new QName("http://impl.webservice.dl.app.bsbr.altec.com/", "createResponse");
    private final static QName _Validate_QNAME = new QName("http://impl.webservice.dl.app.bsbr.altec.com/", "validate");
    private final static QName _ValidateResponse_QNAME = new QName("http://impl.webservice.dl.app.bsbr.altec.com/", "validateResponse");

    public ObjectFactory() {
    }

    public TicketRequest createTicketRequest() {
        return new TicketRequest();
    }

    public TicketRequest.Dados createTicketRequestDados() {
        return new TicketRequest.Dados();
    }

    public ValidateTicketResponse createValidateTicketResponse() {
        return new ValidateTicketResponse();
    }

    public ValidateTicketResponse.Dados createValidateTicketResponseDados() {
        return new ValidateTicketResponse.Dados();
    }

    public Create createCreate() {
        return new Create();
    }

    public CreateResponse createCreateResponse() {
        return new CreateResponse();
    }

    public Validate createValidate() {
        return new Validate();
    }

    public ValidateResponse createValidateResponse() {
        return new ValidateResponse();
    }

    public ValidateTicketRequest createValidateTicketRequest() {
        return new ValidateTicketRequest();
    }

    public TicketResponse createTicketResponse() {
        return new TicketResponse();
    }

    public TicketRequest.Dados.Entry createTicketRequestDadosEntry() {
        return new TicketRequest.Dados.Entry();
    }

    public ValidateTicketResponse.Dados.Entry createValidateTicketResponseDadosEntry() {
        return new ValidateTicketResponse.Dados.Entry();
    }

    @XmlElementDecl(namespace = "http://impl.webservice.dl.app.bsbr.altec.com/", name = "create")
    public JAXBElement<Create> createCreate(Create value) {
        return new JAXBElement<Create>(_Create_QNAME, Create.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.dl.app.bsbr.altec.com/", name = "createResponse")
    public JAXBElement<CreateResponse> createCreateResponse(CreateResponse value) {
        return new JAXBElement<CreateResponse>(_CreateResponse_QNAME, CreateResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.dl.app.bsbr.altec.com/", name = "validate")
    public JAXBElement<Validate> createValidate(Validate value) {
        return new JAXBElement<Validate>(_Validate_QNAME, Validate.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.dl.app.bsbr.altec.com/", name = "validateResponse")
    public JAXBElement<ValidateResponse> createValidateResponse(ValidateResponse value) {
        return new JAXBElement<ValidateResponse>(_ValidateResponse_QNAME, ValidateResponse.class, null, value);
    }

}
