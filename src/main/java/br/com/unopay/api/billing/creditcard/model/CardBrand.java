package br.com.unopay.api.billing.creditcard.model;

public enum CardBrand {

    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DINERS("Diners"),
    ELO("Elo"),
    UNKNOWN("Unkown");

    private String name;

    private CardBrand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CardBrand fromCardNumber(final String creditCardNumber) {

        String visa = "^4[0-9]{12}(?:[0-9]{3})?$";
        String master = "^5[1-5][0-9]{14}$";
        String americanExpress = "^3[47][0-9]{13}$";
        String diners = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";
        String elo = "^((((636368)|(438935)|(504175)|(451416)|(636297))[0-9]{10})|(((5067)|(4576)|(4011))[0-9]{12}))$";

        if (creditCardNumber.matches(visa)) {
            return CardBrand.VISA;
        } else if (creditCardNumber.matches(master)) {
            return CardBrand.MASTERCARD;
        } else if (creditCardNumber.matches(americanExpress)) {
            return CardBrand.AMEX;
        } else if (creditCardNumber.matches(diners)) {
            return CardBrand.DINERS;
        } else if (creditCardNumber.matches(elo)) {
            return CardBrand.ELO;
        }

        return UNKNOWN;

    }

}
