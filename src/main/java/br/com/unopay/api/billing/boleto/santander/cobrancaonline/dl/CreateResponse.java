package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createResponse", propOrder = {
    "ticketResponse"
},namespace = "createResponse")
public class CreateResponse {

    @XmlElement(name = "TicketResponse")
    protected TicketResponse ticketResponse;

    public TicketResponse getTicketResponse() {
        return ticketResponse;
    }

    public void setTicketResponse(TicketResponse value) {
        this.ticketResponse = value;
    }

}
