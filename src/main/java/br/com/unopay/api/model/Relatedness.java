package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

import java.util.Arrays;
import java.util.List;

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
        String lowerCaseRelatedness = relatedness.toLowerCase();
        List<Relatedness> values = Arrays.asList(Relatedness.values());
        for(Relatedness value : values) {
            String prefix = value.getDescription().substring(0, 1).toLowerCase();
            if(lowerCaseRelatedness.startsWith(prefix)) {
                return value;
            }
        }
        return null;
    }
}
