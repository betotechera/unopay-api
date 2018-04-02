package br.com.unopay.api.uaa.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.util.Arrays;

public enum RequestOrigin implements DescriptableEnum {
    UNOPAY("unopay"),
    SUPER_SAUDE("Super saude"),
    BACKOFFICE("backoficce");
    private String description;

    RequestOrigin(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static RequestOrigin fromPt(String requestOrigin){
        String lowerCaseRequestOrigin = requestOrigin.toLowerCase();
        return Arrays.stream(RequestOrigin.values()).filter( origin->
                origin.description.equals(lowerCaseRequestOrigin))
                .findFirst().orElse(null);
    }

    public boolean isUnopay() {
        return this.equals(UNOPAY);
    }
}
