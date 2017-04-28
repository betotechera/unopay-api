package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptionEnum;

public enum ProductType implements DescriptionEnum {
    FREIGHT("Frete"), OTHERS("Outros");

    private String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
