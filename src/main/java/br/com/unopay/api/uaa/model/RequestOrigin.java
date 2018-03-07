package br.com.unopay.api.uaa.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum RequestOrigin implements DescriptableEnum {
    UNOPAY("unopay"),
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
        if(lowerCaseRequestOrigin.equals("unopay")) {
            return UNOPAY;
        }
        if(lowerCaseRequestOrigin.equals("backoffice")) {
            return BACKOFFICE;
        }
        return null;
    }
}
