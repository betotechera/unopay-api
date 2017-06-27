package br.com.unopay.api.bacen.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum RecurrencePeriod implements DescriptableEnum{

    DAILY("Diario", "0 0 3 ? * MON-FRI *"),WEEKLY("Semanal", "0 0 3 ? * MON *"),
    BIWEEKLY("Quinzenal", "0 3 0 1,15 * ?"),MONTHLY("Mensal", "0 0 3 ? * MON#1 *");

    private String description;

    private String pattern;

    RecurrencePeriod(String description, String pattern) {
        this.description = description;
        this.pattern = pattern;
    }

    public String getDescription() {
        return description;
    }

    public String getPattern() {
        return pattern;
    }
}
