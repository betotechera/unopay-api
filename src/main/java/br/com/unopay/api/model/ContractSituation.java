package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum ContractSituation implements DescriptibleEnum {
    ACTIVE("Ativo"), SUSPENDED("Suspenso"),CANCELLED("Canclado"),FINALIZED("Finalizado"),EXPIRED("Expirado");

    private String description;

    ContractSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
