package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registraTitulo", propOrder = {
    "dto"
},namespace = "registraTitulo")
public class RegistraTitulo {

    protected TituloGenericRequest dto;

    public TituloGenericRequest getDto() {
        return dto;
    }

    public void setDto(TituloGenericRequest value) {
        this.dto = value;
    }

}
