package br.com.unopay.api.fileuploader.service;

import br.com.unopay.api.fileuploader.util.Slug;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

public abstract class AbstractUploadService {
    private static final Pattern IMAGE_EXTENSION_PATTERN = Pattern
            .compile("\\.(png|jpe?g|gif|bmp|svg)$", Pattern.CASE_INSENSITIVE);

    protected String slugfyIgnoringExtension(String originalFilename) {
        Matcher matcher = IMAGE_EXTENSION_PATTERN.matcher(originalFilename);
        if(matcher.find()) {
            return Slug.makeSlug(matcher.replaceFirst("")) + matcher.group(0).toLowerCase(ENGLISH);
        } else {
            return Slug.makeSlug(originalFilename);
        }
    }

    public abstract String getRelativePath(String service);
}
