package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentSource implements DescriptableEnum {

    CONTRACTOR("Contratante"), ESTABLISHMENT("Estabelecimento"), PAMCARY("Pamcary");

    private String description;

    PaymentSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
