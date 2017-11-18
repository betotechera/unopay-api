package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validate", propOrder = {
    "validateTicketRequest"
},namespace = "validate")
public class Validate {

    @XmlElement(name = "ValidateTicketRequest")
    protected ValidateTicketRequest validateTicketRequest;

    public ValidateTicketRequest getValidateTicketRequest() {
        return validateTicketRequest;
    }

    public void setValidateTicketRequest(ValidateTicketRequest value) {
        this.validateTicketRequest = value;
    }

}
