package br.com.unopay.api.fileuploader.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Slug {

    private static final Pattern VALID_CHARACTERS = Pattern.compile("[^a-z\\d\\-]", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHITESPACE = Pattern.compile("(\\s+|_+)");
    private static final Pattern DASH_REPLACE_PATTERN = Pattern.compile("(^-+|-+$)");
    private static final Pattern MULTIPLE_DASHES_PATTERN = Pattern.compile("-+");

    public static String makeSlug(String value) {

        if(value == null || "".equals(value.trim())) {
            return "";
        }

        value = value.toLowerCase();
        value = value.replaceAll("\\.", " ");
        value = value.replaceAll("\\+", " ");
        value = WHITESPACE.matcher(value).replaceAll("-");
        value = value.replaceAll("&", "e");
        value = DASH_REPLACE_PATTERN.matcher(value).replaceAll("");
        value = Normalizer.normalize(value, Normalizer.Form.NFD);
        value = VALID_CHARACTERS.matcher(value).replaceAll("");
        value = MULTIPLE_DASHES_PATTERN.matcher(value).replaceAll("-");
        return value;
    }
}