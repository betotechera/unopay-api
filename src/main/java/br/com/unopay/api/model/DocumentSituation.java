package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum DocumentSituation implements DescriptableEnum{

    APPROVED("Documentacao aprovada","1"), INCOMPLETE("Documentacao incompleta","2"),
    CAVEAT("Documentacao com ressalva.","3"), PENDING("Documentação pendente","4"),
    CANCELED("Documentaçao cancelada", "5");

    private String description;

    public String getCode() {
        return code;
    }

    private String code;

    DocumentSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static DocumentSituation from(String compareCode){
        return Stream.of(DocumentSituation.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .findFirst().orElse(null);
    }
}
