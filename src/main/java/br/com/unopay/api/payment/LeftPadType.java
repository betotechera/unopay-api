package br.com.unopay.api.payment;

public enum LeftPadType {
    SPACE(" "), ZERO("0");

    public String getValue() {
        return value;
    }

    private String value;

    LeftPadType(String value) {
        this.value = value;
    }
}
