package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum ComplementaryTravelDocumentType implements DescriptableEnum {

    PAL("Recibo de Entrega de Pallet", "1"), PFI("Passe Fiscal", "22"),
    IBA("Licença IBAMA","23"), CTE("CT-e Assinado","24"), NFC("Canhoto NF Cliente","25"), CTB("CT-b","26");

    private String description;

    public String getCode() {
        return code;
    }

    private String code;

    ComplementaryTravelDocumentType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static ComplementaryTravelDocumentType from(String compareCode){
        return Stream.of(ComplementaryTravelDocumentType.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .reduce((firs, last) -> last).orElse(null);
    }
}
