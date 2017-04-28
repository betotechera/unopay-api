package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum AccreditedNetworkType implements DescriptionEnum {

    SUPPLY("Rede de Abastecimento/Quitação de Frete"), TOLL("Rede de Pedágio Eletrônico");

    private String description;

    AccreditedNetworkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
