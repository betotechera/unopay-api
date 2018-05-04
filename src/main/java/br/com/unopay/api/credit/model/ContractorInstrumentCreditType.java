package br.com.unopay.api.credit.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ContractorInstrumentCreditType implements DescriptableEnum {
    NORMAL("Crédito normal"), BONUS("Crédito bonus");
    private String description;

    ContractorInstrumentCreditType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
