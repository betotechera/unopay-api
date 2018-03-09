package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum Relatedness implements DescriptableEnum {
    GRANDPARENT("Avô(ó)"),AUNT_UNCLE("Tio(a)"), MOTHER("Mãe"), FATHER("Pai"), SIBLING("Irmão(ã)"),
    DAUGHTER_SON("Filho(a)"), NIECE_NEPHEW("Sobrinho(a)"), GRANDCHILD("Neto(a)");

    private String description;

    Relatedness(String description){
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static Relatedness fromPt(String relatedness){
        String lowerCaseGender = relatedness.toLowerCase();
        if(lowerCaseGender.startsWith("av")) {
            return GRANDPARENT;
        }
        if(lowerCaseGender.startsWith("ti")) {
            return AUNT_UNCLE;
        }
        if(lowerCaseGender.startsWith("m")) {
            return MOTHER;
        }
        if(lowerCaseGender.equals("pai")) {
            return FATHER;
        }
        if(lowerCaseGender.startsWith("irm")) {
            return SIBLING;
        }
        if(lowerCaseGender.startsWith("filh")) {
            return DAUGHTER_SON;
        }
        if(lowerCaseGender.startsWith("sobrinh")) {
            return NIECE_NEPHEW;
        }
        return GRANDCHILD;
    }
}
