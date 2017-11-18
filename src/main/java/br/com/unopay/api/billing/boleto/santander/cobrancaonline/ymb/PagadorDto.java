package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagadorDto", propOrder = {
    "bairro",
    "cep",
    "cidade",
    "ender",
    "nome",
    "numDoc",
    "tpDoc",
    "uf"
},namespace = "pagadorDto")
public class PagadorDto {

    protected String bairro;
    protected String cep;
    protected String cidade;
    protected String ender;
    protected String nome;
    protected String numDoc;
    protected String tpDoc;
    protected String uf;

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String value) {
        this.bairro = value;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String value) {
        this.cep = value;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String value) {
        this.cidade = value;
    }

    public String getEnder() {
        return ender;
    }

    public void setEnder(String value) {
        this.ender = value;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String value) {
        this.nome = value;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setNumDoc(String value) {
        this.numDoc = value;
    }

    public String getTpDoc() {
        return tpDoc;
    }

    public void setTpDoc(String value) {
        this.tpDoc = value;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String value) {
        this.uf = value;
    }

}
