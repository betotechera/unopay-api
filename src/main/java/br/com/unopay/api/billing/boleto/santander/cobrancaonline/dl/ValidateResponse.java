package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validateResponse", propOrder = {
    "validateTicketResponse"
},namespace = "validateResponse")
public class ValidateResponse {

    @XmlElement(name = "ValidateTicketResponse")
    protected ValidateTicketResponse validateTicketResponse;

    public ValidateTicketResponse getValidateTicketResponse() {
        return validateTicketResponse;
    }

    public void setValidateTicketResponse(ValidateTicketResponse value) {
        this.validateTicketResponse = value;
    }

}
