package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "convenioDto", propOrder = {
    "codBanco",
    "codConv"
},namespace = "convenioDto")
public class ConvenioDto {

    protected String codBanco;
    protected String codConv;

    public String getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(String value) {
        this.codBanco = value;
    }

    public String getCodConv() {
        return codConv;
    }

    public void setCodConv(String value) {
        this.codConv = value;
    }

}
