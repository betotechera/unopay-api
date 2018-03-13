package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

import java.text.Normalizer;
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
        String normalizedRelatedness = normalize(relatedness);
        List<Relatedness> values = Arrays.asList(Relatedness.values());
        values.stream().map(value -> normalize(value)).filter()
        for(Relatedness value : values) {
            String prefix = normalize(value.getDescription().substring(0, 2));
            if(normalizedRelatedness.startsWith(prefix)) {
                return value;
            }
        }
        return null;
    }

    private static String normalize(String src) {
        String unaccented = Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return unaccented.toLowerCase();
    }
}
