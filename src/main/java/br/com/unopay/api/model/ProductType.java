package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum ProductType implements DescriptibleEnum {
    FREIGHT("Frete"), OTHERS("Outros");

    private String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
