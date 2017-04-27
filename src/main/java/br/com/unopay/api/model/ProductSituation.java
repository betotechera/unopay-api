package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum ProductSituation implements DescriptibleEnum {

    ACTIVE("Ativo"), SUSPENDED("Suspenso"), CANCELED("Cancelado");

    private String description;

    ProductSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
