package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.PurposeDeserialize;
import br.com.unopay.api.bacen.util.rest.PurposeSerialize;
import br.com.unopay.api.bacen.util.rest.UserRelationshipDeserialize;
import br.com.unopay.api.bacen.util.rest.UserRelationshipSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = UserRelationshipDeserialize.class)
@JsonSerialize(using = UserRelationshipSerialize.class)

public enum UserRelationship {

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
