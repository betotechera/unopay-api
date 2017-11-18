package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticketRequest", propOrder = {
    "dados",
    "expiracao",
    "sistema"
})
public class TicketRequest {

    @XmlElement(required = true)
    protected TicketRequest.Dados dados;
    protected int expiracao;
    protected String sistema;

    public TicketRequest.Dados getDados() {
        return dados;
    }

    public void setDados(TicketRequest.Dados value) {
        this.dados = value;
    }

    public int getExpiracao() {
        return expiracao;
    }

    public void setExpiracao(int value) {
        this.expiracao = value;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String value) {
        this.sistema = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class Dados {

        protected List<TicketRequest.Dados.Entry> entry;

        public List<TicketRequest.Dados.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<TicketRequest.Dados.Entry>();
            }
            return this.entry;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "entry", propOrder = {
            "key",
            "value"
        },namespace = "entry")
        public static class Entry {

            protected String key;
            protected String value;

            public String getKey() {
                return key;
            }

            public void setKey(String value) {
                this.key = value;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

        }
    }
}
