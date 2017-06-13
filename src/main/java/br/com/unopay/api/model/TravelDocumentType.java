package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum TravelDocumentType implements DescriptableEnum{

    MAN("MANIFESTO", "1"), ROM("ROMANEIO", "2"), PVIA("PLANO DE VIAGEM", "3"), AWB("AWB", "4"), CON("CONHECIMENTO", "5"),
    NFIS("NOTA FISCAL", "6"), DCLI("DOCUMENTO PROPRIO DO CLIENTE", "7"), NPED("NUMERO PEDIDO", "8"),
    OVEN("ORDEM DE VENDA", "9"),  NLOA("NUMERO LOAD", "10"), OCOL("ORDEM DE COLETA", "11"),
    ATCR("AUTORIZACAO DE CARREGAMENTO", "12"), ATSD("AUTORIZACAO DE SAIDA", "13");

    private String description;
    private String code;

    TravelDocumentType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
    public String getGetCode() {
        return code;
    }

}
