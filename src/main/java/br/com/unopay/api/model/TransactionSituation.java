package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum TransactionSituation implements DescriptableEnum {
    AUTHORIZED("Autorizada"),UNDETERMINED("Indeterminada"),
    AUTHORIZATION_ERROR("Erro na autorizaçao"), FINALIZED("Finalizada"), LOADING_ERROR("Erro na carga");

    private String description;

    TransactionSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
