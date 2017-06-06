package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum CargoProfile implements DescriptableEnum {

    DRY_CARGO("Carga seca"), IN_BULK("A granel");

    private String description;

    CargoProfile(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
