package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tituloGenericResponse", propOrder = {
    "codcede",
    "convenio",
    "descricaoErro",
    "dtNsu",
    "estacao",
    "nsu",
    "pagador",
    "situacao",
    "titulo",
    "tpAmbiente"
},namespace = "tituloGenericResponse")
public class TituloGenericResponse {

    protected String codcede;
    protected ConvenioDto convenio;
    protected String descricaoErro;
    protected String dtNsu;
    protected String estacao;
    protected String nsu;
    protected PagadorDto pagador;
    protected String situacao;
    protected TituloDto titulo;
    protected String tpAmbiente;

    public String getCodcede() {
        return codcede;
    }

    public void setCodcede(String value) {
        this.codcede = value;
    }

    public ConvenioDto getConvenio() {
        return convenio;
    }

    public void setConvenio(ConvenioDto value) {
        this.convenio = value;
    }

    public String getDescricaoErro() {
        return descricaoErro;
    }

    public void setDescricaoErro(String value) {
        this.descricaoErro = value;
    }

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

    public PagadorDto getPagador() {
        return pagador;
    }

    public void setPagador(PagadorDto value) {
        this.pagador = value;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String value) {
        this.situacao = value;
    }

    public TituloDto getTitulo() {
        return titulo;
    }

    public void setTitulo(TituloDto value) {
        this.titulo = value;
    }

    public String getTpAmbiente() {
        return tpAmbiente;
    }

    public void setTpAmbiente(String value) {
        this.tpAmbiente = value;
    }

}
