package br.com.unopay.api.model;

import br.com.unopay.api.util.rest.CompanyTypeDeserializer;
import br.com.unopay.api.util.rest.CompanyTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = CompanyTypeDeserializer.class)
@JsonSerialize(using = CompanyTypeSerializer.class)
public enum CompanyType {

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
