package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registraTituloResponse", propOrder = {
    "_return"
},namespace = "registraTituloResponse")
public class RegistraTituloResponse {

    @XmlElement(name = "return")
    protected TituloGenericResponse _return;

    public TituloGenericResponse getReturn() {
        return _return;
    }

    public void setReturn(TituloGenericResponse value) {
        this._return = value;
    }

}
