package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Gender implements DescriptableEnum {

    WOMAN("Mulher"), MAN("Homem");

    private String description;

    Gender(String description){
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
