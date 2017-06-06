package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ReasonReceiptSituation implements DescriptableEnum{

    DOCUMENTATION_OK("Documentacao Ok"), INCOMPLETE_DOCUMENTATION("Documentacao incompleta"),
    CAVEAT_DOCUMENTATION("Documentacao com ressalva."), GAVE_UP("Desistencia");

    private String description;

    ReasonReceiptSituation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
