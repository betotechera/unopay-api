package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public enum Relatedness implements DescriptableEnum {
    GRANDPARENT("Avô(ó)"),AUNT_UNCLE("Tio(a)"), MOTHER("Mãe"), FATHER("Pai"), SIBLING("Irmão(ã)"),
    DAUGHTER_SON("Filho(a)"), NIECE_NEPHEW("Sobrinho(a)"), GRANDCHILD("Neto(a)");

    private String description;
    private static String REMOVE_ACCENT = "[^\\p{ASCII}]";
    private static int FIRST = 0;
    private static int SECOND = 2;

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
                .orElse(null);
    }

    private static String prefix(String relatedness) {
        return normalize(relatedness.substring(FIRST, SECOND));
    }

    private static String normalize(String src) {
        if(src == null) {
            return "";
        }
        String unaccented = Normalizer.normalize(src, Normalizer.Form.NFD)
                .replaceAll(REMOVE_ACCENT, "");
        return unaccented.toLowerCase();
    }
}
