package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _WebServiceException_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "WebServiceException");
    private final static QName _ConsultaTitulo_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "consultaTitulo");
    private final static QName _ConsultaTituloResponse_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "consultaTituloResponse");
    private final static QName _GetVersion_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "getVersion");
    private final static QName _GetVersionResponse_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "getVersionResponse");
    private final static QName _RegistraTitulo_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "registraTitulo");
    private final static QName _RegistraTituloResponse_QNAME = new QName("http://impl.webservice.ymb.app.bsbr.altec.com/", "registraTituloResponse");

    public ObjectFactory() {
    }

    public FaultInfo createFaultInfo() {
        return new FaultInfo();
    }

    public ConsultaTitulo createConsultaTitulo() {
        return new ConsultaTitulo();
    }

    public ConsultaTituloResponse createConsultaTituloResponse() {
        return new ConsultaTituloResponse();
    }

    public GetVersion createGetVersion() {
        return new GetVersion();
    }

    public GetVersionResponse createGetVersionResponse() {
        return new GetVersionResponse();
    }

    public RegistraTitulo createRegistraTitulo() {
        return new RegistraTitulo();
    }

    public RegistraTituloResponse createRegistraTituloResponse() {
        return new RegistraTituloResponse();
    }

    public TituloGenericRequest createTituloGenericRequest() {
        return new TituloGenericRequest();
    }

    public TituloGenericResponse createTituloGenericResponse() {
        return new TituloGenericResponse();
    }

    public ConvenioDto createConvenioDto() {
        return new ConvenioDto();
    }

    public PagadorDto createPagadorDto() {
        return new PagadorDto();
    }

    public TituloDto createTituloDto() {
        return new TituloDto();
    }

    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "WebServiceException")
    public JAXBElement<FaultInfo> createWebServiceException(FaultInfo value) {
        return new JAXBElement<FaultInfo>(_WebServiceException_QNAME, FaultInfo.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "consultaTitulo")
    public JAXBElement<ConsultaTitulo> createConsultaTitulo(ConsultaTitulo value) {
        return new JAXBElement<ConsultaTitulo>(_ConsultaTitulo_QNAME, ConsultaTitulo.class, null, value);
    }
    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "consultaTituloResponse")
    public JAXBElement<ConsultaTituloResponse> createConsultaTituloResponse(ConsultaTituloResponse value) {
        return new JAXBElement<ConsultaTituloResponse>(_ConsultaTituloResponse_QNAME, ConsultaTituloResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "getVersion")
    public JAXBElement<GetVersion> createGetVersion(GetVersion value) {
        return new JAXBElement<GetVersion>(_GetVersion_QNAME, GetVersion.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "getVersionResponse")
    public JAXBElement<GetVersionResponse> createGetVersionResponse(GetVersionResponse value) {
        return new JAXBElement<GetVersionResponse>(_GetVersionResponse_QNAME, GetVersionResponse.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "registraTitulo")
    public JAXBElement<RegistraTitulo> createRegistraTitulo(RegistraTitulo value) {
        return new JAXBElement<RegistraTitulo>(_RegistraTitulo_QNAME, RegistraTitulo.class, null, value);
    }

    @XmlElementDecl(namespace = "http://impl.webservice.ymb.app.bsbr.altec.com/", name = "registraTituloResponse")
    public JAXBElement<RegistraTituloResponse> createRegistraTituloResponse(RegistraTituloResponse value) {
        return new JAXBElement<RegistraTituloResponse>(_RegistraTituloResponse_QNAME, RegistraTituloResponse.class, null, value);
    }

}
