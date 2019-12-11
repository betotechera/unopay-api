package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Gender implements DescriptableEnum {

    FEMALE("Feminino"), MALE("Masculino");

    private String description;

    Gender(String description){
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static Gender fromPt(String gender){
        if(gender != null) {
            String lowerCaseGender = gender.toLowerCase();
            return (lowerCaseGender.startsWith("f")) ? FEMALE : MALE;
        }
        return null;
    }
}
