package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum DocumentTravelType implements DescriptableEnum{

    MAN("MANIFESTO"), ROM("ROMANEIO"), PVIA("PLANO DE VIAGEM"), AWB("AWB"), CON("CONHECIMENTO"), NFIS("NOTA FISCAL"),
    DCLI("DOCUMENTO PROPRIO DO CLIENTE"), NPED("NUMERO PEDIDO"), OVEN("ORDEM DE VENDA"),
    NLOA("NUMERO LOAD"), OCOL("ORDEM DE COLETA"), ATCR("AUTORIZACAO DE CARREGAMENTO"), ATSD("AUTORIZACAO DE SAIDA");

    private String description;

    DocumentTravelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }



}
