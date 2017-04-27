package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum UserRelationship implements DescriptibleEnum {

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
