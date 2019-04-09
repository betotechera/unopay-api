package br.com.unopay.api.network.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Weekday implements DescriptableEnum {

    MONDAY("Segunda"), TUESDAY("Terça"), WEDNESDAY("Quarta"), THURSDAY("Quinta"), FRIDAY("Sexta"), SATURDAY("Sabado"), SUNDAY("Domingo");

    private String description;

    Weekday(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
