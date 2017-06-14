package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum TravelDocumentSituation implements DescriptableEnum {

    DIGITIZED("Digitalizado","1"), RETIRED("Retirado","2");

    private String description;
    private String code;

    TravelDocumentSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static TravelDocumentSituation from(String compareCode){
        return Stream.of(TravelDocumentSituation.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .findFirst().orElse(null);
    }

}
