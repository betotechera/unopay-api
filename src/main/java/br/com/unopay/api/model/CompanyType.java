package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum CompanyType implements DescriptableEnum {

    LIMITED_PARTNERSHIP("Sociedade por quotas de responsabilidade limitada"),
    STOCK_COMPANY("Sociedade por ações - S.A"),
    COOPERATIVE_SOCIETY("Sociedade simples / cooperativa"),
    MICRO("Micro empresa de pequeno porte"),
    FREELANCE("Autonomo");
    private String description;

    CompanyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
