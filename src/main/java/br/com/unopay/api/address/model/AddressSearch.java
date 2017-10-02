package br.com.unopay.api.address.model;

import lombok.Data;

@Data
public class AddressSearch {

    public static final long serialVersionUID = 1L;

    public AddressSearch(){}

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

}
