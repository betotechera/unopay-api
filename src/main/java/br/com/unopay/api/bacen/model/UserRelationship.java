package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum UserRelationship implements DescriptableEnum {

    PREPAID("Conta de pagamento Pre-paga"),
    POSTPAID("Conta de pagamento Pos-paga"),
    DEPOSIT("Conta de deposito a vista"),
    EVENTUAL("Relacionamento eventual");

    UserRelationship(String description) {
        this.description = description;
    }

    private String description;

    public String getDescription() {
        return description;
    }
}
