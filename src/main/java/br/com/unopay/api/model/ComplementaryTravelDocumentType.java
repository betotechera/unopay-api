package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum ComplementaryTravelDocumentType implements DescriptableEnum {

    PAL("Recibo de Entrega de Pallet"), PFI("Passe Fiscal"),
    IBA("Licença IBAMA"), CTE("CT-e Assinado"), NFC("Canhoto NF Cliente");

    private String description;

    ComplementaryTravelDocumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
