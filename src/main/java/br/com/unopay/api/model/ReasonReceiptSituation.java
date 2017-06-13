package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum ReasonReceiptSituation implements DescriptableEnum{

    DOCUMENTATION_OK("Documentacao Ok","1"), INCOMPLETE_DOCUMENTATION("Documentacao incompleta","2"),
    CAVEAT_DOCUMENTATION("Documentacao com ressalva.","3"), GAVE_UP("Desistencia","4");

    private String description;
    private String code;

    ReasonReceiptSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static ReasonReceiptSituation from(String compareCode){
        return Stream.of(ReasonReceiptSituation.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .reduce((firs, last) -> last).orElse(null);
    }
}
