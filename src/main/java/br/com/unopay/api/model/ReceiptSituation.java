package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum ReceiptSituation implements DescriptableEnum {

    ACCEPTED("Aceita","1"), REFUSED("Recusada","2");

    private String description;

    public String getCode() {
        return code;
    }

    private String code;

    ReceiptSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static ReceiptSituation from(String compareCode){
        return Stream.of(ReceiptSituation.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .reduce((firs, last) -> last).orElse(null);
    }
}