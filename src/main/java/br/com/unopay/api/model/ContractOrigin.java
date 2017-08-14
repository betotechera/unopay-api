package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ContractOrigin implements DescriptableEnum {
    APPLICATION("Aplicaçao"), EXTERNAL_SYSTEM("Sistema externo"), PARTNER("Parceiro");

    private String description;

    ContractOrigin(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
