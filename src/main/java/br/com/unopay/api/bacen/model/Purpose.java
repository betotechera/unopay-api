package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum Purpose implements DescriptionEnum {

    BUY("Compra"), TRANSFER("Tranferencia");

    private String description;

    Purpose(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
