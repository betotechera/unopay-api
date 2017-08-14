package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Segment implements DescriptableEnum{

    HEALTH("Saude"), TRANSPORT("Transporte");

    private String description;

    Segment(String description){

        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
