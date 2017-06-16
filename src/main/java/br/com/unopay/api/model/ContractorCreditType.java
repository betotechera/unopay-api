package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ContractorCreditType implements DescriptableEnum {

    PAY_ADVANCE("Adiantamento", "0"), FINAL_PAYMENT("Saldo Final", "1");

    private String description;
    private String code;

    ContractorCreditType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
