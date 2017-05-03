package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Scope implements DescriptableEnum {
    DOMESTIC("Domestico"), INTERNATIONAL("Internacional");

    private String description;

    Scope(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
