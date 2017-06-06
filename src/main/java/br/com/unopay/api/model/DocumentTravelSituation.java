package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum DocumentTravelSituation implements DescriptableEnum {

    DIGITIZED("Digitalizado"), RETIRED("Retirado");

    private String description;

    DocumentTravelSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
