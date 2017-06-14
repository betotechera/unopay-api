package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum ReceiptStep implements DescriptableEnum {

    CONSULTED("Consultado","1"), COLLECTED("Coletado","2"),
    RECEIVED("Recebido","3"), DIGITIZED("Digitalizado","4"),
    ARCHIVED("Arquivado","5"), SENT("Enviado","6");

    private String description;
    private String code;

    public String getCode() {
        return code;
    }

    ReceiptStep(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static ReceiptStep from(String compareCode){
        return Stream.of(ReceiptStep.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .findFirst().orElse(null);
    }
}
