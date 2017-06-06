package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum DocumentCaveat implements DescriptableEnum{

    S("Sim"),N("Nao");

    private String description;

    DocumentCaveat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
