package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum DocumentTravelSituation implements DescriptableEnum {

    DIGITIZED("Digitalizado","1"), RETIRED("Retirado","2");

    private String description;
    private String code;

    DocumentTravelSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static DocumentTravelSituation from(String compareCode){
        return Stream.of(DocumentTravelSituation.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .reduce((firs, last) -> last).orElse(null);
    }

}
