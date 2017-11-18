package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tituloDto", propOrder = {
    "aceito",
    "cdBarra",
    "dtEmissao",
    "dtEntr",
    "dtLimiDesc",
    "dtVencto",
    "especie",
    "linDig",
    "mensagem",
    "nossoNumero",
    "pcJuro",
    "pcMulta",
    "qtDiasBaixa",
    "qtDiasMulta",
    "qtDiasProtesto",
    "seuNumero",
    "tpDesc",
    "tpProtesto",
    "vlAbatimento",
    "vlDesc",
    "vlNominal"
},namespace = "tituloDto")
public class TituloDto {

    protected String aceito;
    protected String cdBarra;
    protected String dtEmissao;
    protected String dtEntr;
    protected String dtLimiDesc;
    protected String dtVencto;
    protected String especie;
    protected String linDig;
    protected String mensagem;
    protected String nossoNumero;
    protected String pcJuro;
    protected String pcMulta;
    protected String qtDiasBaixa;
    protected String qtDiasMulta;
    protected String qtDiasProtesto;
    protected String seuNumero;
    protected String tpDesc;
    protected String tpProtesto;
    protected String vlAbatimento;
    protected String vlDesc;
    protected String vlNominal;

    public String getAceito() {
        return aceito;
    }

    public void setAceito(String value) {
        this.aceito = value;
    }

    public String getCdBarra() {
        return cdBarra;
    }

    public void setCdBarra(String value) {
        this.cdBarra = value;
    }

    public String getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(String value) {
        this.dtEmissao = value;
    }

    public String getDtEntr() {
        return dtEntr;
    }

    public void setDtEntr(String value) {
        this.dtEntr = value;
    }

    public String getDtLimiDesc() {
        return dtLimiDesc;
    }

    public void setDtLimiDesc(String value) {
        this.dtLimiDesc = value;
    }

    public String getDtVencto() {
        return dtVencto;
    }

    public void setDtVencto(String value) {
        this.dtVencto = value;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String value) {
        this.especie = value;
    }

    public String getLinDig() {
        return linDig;
    }

    public void setLinDig(String value) {
        this.linDig = value;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String value) {
        this.mensagem = value;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String value) {
        this.nossoNumero = value;
    }

    public String getPcJuro() {
        return pcJuro;
    }

    public void setPcJuro(String value) {
        this.pcJuro = value;
    }

    public String getPcMulta() {
        return pcMulta;
    }

    public void setPcMulta(String value) {
        this.pcMulta = value;
    }

    public String getQtDiasBaixa() {
        return qtDiasBaixa;
    }

    public void setQtDiasBaixa(String value) {
        this.qtDiasBaixa = value;
    }

    public String getQtDiasMulta() {
        return qtDiasMulta;
    }

    public void setQtDiasMulta(String value) {
        this.qtDiasMulta = value;
    }

    public String getQtDiasProtesto() {
        return qtDiasProtesto;
    }

    public void setQtDiasProtesto(String value) {
        this.qtDiasProtesto = value;
    }

    public String getSeuNumero() {
        return seuNumero;
    }

    public void setSeuNumero(String value) {
        this.seuNumero = value;
    }

    public String getTpDesc() {
        return tpDesc;
    }

    public void setTpDesc(String value) {
        this.tpDesc = value;
    }

    public String getTpProtesto() {
        return tpProtesto;
    }

    public void setTpProtesto(String value) {
        this.tpProtesto = value;
    }

    public String getVlAbatimento() {
        return vlAbatimento;
    }

    public void setVlAbatimento(String value) {
        this.vlAbatimento = value;
    }

    public String getVlDesc() {
        return vlDesc;
    }

    public void setVlDesc(String value) {
        this.vlDesc = value;
    }
    public String getVlNominal() {
        return vlNominal;
    }

    public void setVlNominal(String value) {
        this.vlNominal = value;
    }

}
