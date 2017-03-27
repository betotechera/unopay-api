package br.com.unopay.api.util;

import java.util.Collection;
import java.util.stream.Collectors;

public class StringJoiner {
    private static final String DEFAULT_DELIMITER = ",";

    public static String join(Collection<String> strings) {
        return join(strings, DEFAULT_DELIMITER);
    }

    public static String join(Collection<String> strings, String delimiter) {
        if(strings != null && !strings.isEmpty()) {
            return strings.stream().collect(Collectors.joining(delimiter));
        } else {
            return "";
        }
    }
}