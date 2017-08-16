package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum PaymentSource implements DescriptableEnum {

    HIRER("Contratante","1"), ESTABLISHMENT("Estabelecimento","2");

    private String description;
    private String code;

    PaymentSource(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentSource from(String compareCode){
        return Stream.of(PaymentSource.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .findFirst().orElse(null);
    }
}
