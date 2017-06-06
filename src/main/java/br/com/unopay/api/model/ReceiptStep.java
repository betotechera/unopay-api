package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ReceiptStep implements DescriptableEnum {

    CONSULTED("Consultado"), COLLECTED("Coletado"),
    RECEIVED("Recebido"), DIGITIZED("Digitalizado"),
    ARCHIVED("Arquivado"), SENT("Enviado");

    private String description;

    ReceiptStep(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
