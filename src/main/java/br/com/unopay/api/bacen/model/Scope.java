package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum Scope implements DescriptionEnum {
    DOMESTIC("Domestico"), INTERNATIONAL("Internacional");

    private String description;

    Scope(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
