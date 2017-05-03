package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum BankAccountType implements DescriptableEnum {
    CURRENT("Corrente"), SAVINGS("Poupança");
    private String description;

    BankAccountType(String description){
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
