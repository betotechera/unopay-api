package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum Purpose implements DescriptibleEnum{

    BUY("Compra"), TRANSFER("Tranferencia");

    private String description;

    Purpose(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
