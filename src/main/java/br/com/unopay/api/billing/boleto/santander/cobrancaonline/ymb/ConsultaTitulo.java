package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaTitulo", propOrder = {
    "dto"
},namespace = "consultaTitulo")
public class ConsultaTitulo {

    protected TituloGenericRequest dto;

    public TituloGenericRequest getDto() {
        return dto;
    }

    public void setDto(TituloGenericRequest value) {
        this.dto = value;
    }

}
