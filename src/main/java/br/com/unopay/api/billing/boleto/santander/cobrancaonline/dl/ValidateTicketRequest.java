package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validateTicketRequest", propOrder = {
    "sistema",
    "ticket"
} ,namespace = "validateTicketRequest")
public class ValidateTicketRequest {

    protected String sistema;
    protected String ticket;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String value) {
        this.sistema = value;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String value) {
        this.ticket = value;
    }

}
