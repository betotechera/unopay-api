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

        String replacedCharacters = value.toLowerCase().replaceAll("\\.", " ").replaceAll("\\+", " ");
        String whitespaceReplaced = WHITESPACE.matcher(replacedCharacters).replaceAll("-").replaceAll("&", "e");
        String dashReplaced = DASH_REPLACE_PATTERN.matcher(whitespaceReplaced).replaceAll("");
        String normalized = Normalizer.normalize(dashReplaced, Normalizer.Form.NFD);
        String validCharacters = VALID_CHARACTERS.matcher(normalized).replaceAll("");
        return MULTIPLE_DASHES_PATTERN.matcher(validCharacters).replaceAll("-");
    }
}