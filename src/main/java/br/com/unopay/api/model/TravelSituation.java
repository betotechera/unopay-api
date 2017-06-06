package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum TravelSituation  implements DescriptableEnum {

    PENDING("PENDENTE"), OPENED("EM ABERTO"), FINISHED("FINALIZADA"), CANCELED("CANCELADA"), ACTIVE("ATIVA");

    private String description;

    TravelSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
