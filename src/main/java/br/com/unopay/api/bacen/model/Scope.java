package br.com.unopay.api.bacen.model;

import br.com.unopay.api.bacen.util.rest.ScopeDeserializer;
import br.com.unopay.api.bacen.util.rest.ScopeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = ScopeDeserializer.class)
@JsonSerialize(using = ScopeSerializer.class)
public enum Scope {
    DOMESTIC("Domestico"), INTERNATIONAL("Internacional");

    private String description;

    Scope(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
