package br.com.unopay.api.notification.engine;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MailValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public MailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean isValid(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}
