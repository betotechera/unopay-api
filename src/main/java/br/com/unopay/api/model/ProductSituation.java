package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum ProductSituation implements DescriptionEnum {

    ACTIVE("Ativo"), SUSPENDED("Suspenso"), CANCELED("Cancelado");

    private String description;

    ProductSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
