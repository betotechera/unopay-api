package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        return values.stream()
                .filter(value -> normalizedRelatedness.startsWith(prefix(value.description)))
                .findFirst()
                .get();
    }

    private static String prefix(String relatedness) {
        return normalize(relatedness.substring(0, 2));
    }

    private static String normalize(String src) {
        String unaccented = Normalizer.normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return unaccented.toLowerCase();
    }
}
