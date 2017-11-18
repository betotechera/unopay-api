package br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validateTicketResponse", propOrder = {
    "dados",
    "message",
    "retCode"
},namespace = "validateTicketResponse")
public class ValidateTicketResponse {

    @XmlElement(required = true)
    protected ValidateTicketResponse.Dados dados;
    protected String message;
    protected int retCode;

    public ValidateTicketResponse.Dados getDados() {
        return dados;
    }

    public void setDados(ValidateTicketResponse.Dados value) {
        this.dados = value;
    }

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


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class Dados {

        protected List<ValidateTicketResponse.Dados.Entry> entry;

        public List<ValidateTicketResponse.Dados.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<ValidateTicketResponse.Dados.Entry>();
            }
            return this.entry;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
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
