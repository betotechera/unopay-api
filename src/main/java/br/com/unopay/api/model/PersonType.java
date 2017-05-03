package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PersonType implements DescriptableEnum {

    PHYSICAL("Pessoa Fisica"), LEGAL("Pessoa Juridica");

    private String description;

    PersonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
