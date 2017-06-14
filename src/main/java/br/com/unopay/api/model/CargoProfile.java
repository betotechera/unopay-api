package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Objects;
import java.util.stream.Stream;

public enum CargoProfile implements DescriptableEnum {

    DRY_CARGO("Carga seca","1"), IN_BULK("A granel","2");

    private String description;

    private String code;

    CargoProfile(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public static CargoProfile from(String compareCode){
        return Stream.of(CargoProfile.values())
                .filter(c-> Objects.equals(c.code, compareCode))
                .findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }
}
