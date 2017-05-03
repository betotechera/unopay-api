package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ProductType implements DescriptableEnum {
    FREIGHT("Frete"), OTHERS("Outros");

    private String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
