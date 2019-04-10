package br.com.unopay.api.network.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum BranchSituation implements DescriptableEnum {

    REGISTERED("Cadastrado"),
    ON_TRYING("Em treinamento"),
    ON_SERVICE("Atendimento liberado"),
    SUSPENDED_SERVICE("Servicoo suspenso e cancelado");

    private String description;

    BranchSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
