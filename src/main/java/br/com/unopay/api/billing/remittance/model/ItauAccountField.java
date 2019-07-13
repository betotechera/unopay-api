package br.com.unopay.api.billing.remittance.model;

import static org.apache.commons.lang3.StringUtils.leftPad;

public class ItauAccountField {

    public static final String S_000000_S_S = "0%s 000000%s %s";
    public static final String S_S_S = "%s %s %s";
    public static final String PAD_STR = "0";
    private RemittancePayee payee;

    public ItauAccountField(RemittancePayee payee) {
        this.payee = payee;
    }

    public String get(){
        if(payee.getBankCode() == 341 || payee.getBankCode() == 409) {
            return String.format(S_000000_S_S, leftPad(payee.getAgency(), 4, PAD_STR), leftPad(payee.getAccountNumber(), 6, PAD_STR), payee.accountDvLastDigit());
        }
        return String.format(S_S_S, leftPad(payee.getAgency(), 5, PAD_STR), leftPad(payee.getAccountNumber(), 12, PAD_STR), payee.accountDvLastDigit());
    }
}
