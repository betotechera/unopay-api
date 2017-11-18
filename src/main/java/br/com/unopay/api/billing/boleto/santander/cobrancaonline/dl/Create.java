package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "create", propOrder = {
    "ticketRequest"
},namespace = "create")
public class Create {

    @XmlElement(name = "TicketRequest")
    protected TicketRequest ticketRequest;

    /**
     * Gets the value of the ticketRequest property.
     *
     * @return
     *     possible object is
     *     {@link TicketRequest }
     *
     */
    public TicketRequest getTicketRequest() {
        return ticketRequest;
    }

    /**
     * Sets the value of the ticketRequest property.
     *
     * @param value
     *     allowed object is
     *     {@link TicketRequest }
     *
     */
    public void setTicketRequest(TicketRequest value) {
        this.ticketRequest = value;
    }

}
