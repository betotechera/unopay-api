package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticketResponse", propOrder = {
    "message",
    "retCode",
    "ticket"
},namespace = "ticketResponse")
public class TicketResponse {

    protected String message;
    protected int retCode;
    protected String ticket;

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int value) {
        this.retCode = value;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String value) {
        this.ticket = value;
    }

}
