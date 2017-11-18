package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tituloGenericRequest", propOrder = {
    "dtNsu",
    "estacao",
    "nsu",
    "ticket",
    "tpAmbiente"
},namespace = "tituloGenericRequest")
public class TituloGenericRequest {

    protected String dtNsu;
    protected String estacao;
    protected String nsu;
    protected String ticket;
    protected String tpAmbiente;

    public String getDtNsu() {
        return dtNsu;
    }

    public void setDtNsu(String value) {
        this.dtNsu = value;
    }

    public String getEstacao() {
        return estacao;
    }

    public void setEstacao(String value) {
        this.estacao = value;
    }

    public String getNsu() {
        return nsu;
    }

    public void setNsu(String value) {
        this.nsu = value;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String value) {
        this.ticket = value;
    }

    public String getTpAmbiente() {
        return tpAmbiente;
    }

    public void setTpAmbiente(String value) {
        this.tpAmbiente = value;
    }

}
