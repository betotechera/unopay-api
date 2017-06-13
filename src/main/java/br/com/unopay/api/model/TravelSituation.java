package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum TravelSituation  implements DescriptableEnum {

    PENDING("PENDENTE","1"), OPENED("EM ABERTO","2"), FINISHED("FINALIZADA","3"),
    CANCELED("CANCELADA","4"), ACTIVE("ATIVA","5");

    private String description;
    private String code;

    TravelSituation(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static TravelSituation from(String compareCode){
        return Stream.of(TravelSituation.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .reduce((firs, last) -> last).orElse(null);
    }
}
