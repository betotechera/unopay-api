package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptionEnum;

public enum ContractSituation implements DescriptionEnum {
    ACTIVE("Ativo"), SUSPENDED("Suspenso"),CANCELLED("Canclado"),FINALIZED("Finalizado"),EXPIRED("Expirado");

    private String description;

    ContractSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
