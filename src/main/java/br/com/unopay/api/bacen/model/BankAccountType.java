package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum BankAccountType implements DescriptibleEnum{
    CURRENT("Corrente"), SAVINGS("Poupança");
    private String description;

    BankAccountType(String description){
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
