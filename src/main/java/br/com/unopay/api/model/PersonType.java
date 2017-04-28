package br.com.unopay.api.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum PersonType implements DescriptionEnum {

    PHYSICAL("Pessoa Fisica"), LEGAL("Pessoa Juridica");

    private String description;

    PersonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
