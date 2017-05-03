package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Purpose implements DescriptableEnum {

    BUY("Compra"), TRANSFER("Tranferencia");

    private String description;

    Purpose(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
