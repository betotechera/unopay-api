package br.com.unopay.api.viacep.model;

import lombok.Data;

@Data
public class CEP {

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private Boolean erro;

    public boolean error() {
        return erro !=null && erro;
    }

    public String unformattedCep() {
        return cep != null ? cep.replaceAll("[^a-zA-Z0-9]+","") : "";
    }
}
