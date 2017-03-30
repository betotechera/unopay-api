package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.AccreditedNetworkTypeDeserializer;
import br.com.unopay.api.bacen.util.rest.AccreditedNetworkTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = AccreditedNetworkTypeDeserializer.class)
@JsonSerialize(using = AccreditedNetworkTypeSerializer.class)
public enum AccreditedNetworkType {

    SUPPLY("Rede de Abastecimento/Quitação de Frete"), TOLL("Rede de Pedágio Eletrônico");

    private String description;

    AccreditedNetworkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
