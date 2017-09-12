package br.com.unopay.api.billing.remittance.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentTransferOption implements DescriptableEnum {

    CURRENT_ACCOUNT_CREDIT("CRÉDITO EM CONTA CORRENTE", "01"),
    PAYCHECK("CHEQUE PAGAMENTO / ADMINISTRATIVO", "02"),
    DOC_TED("DOC/TED", "03"),
    PAY_CARD("CARTÃO SALÁRIO (SOMENTE PARA TIPO DE ERVIÇO = '30'", "04"),
    SAVINGS_ACCOUNT_CREDIT("CRÉDITO EM CONTA POUPANÇA", "05"),
    AVAILABLE("OP À DISPOSIÇÃO", "10"),;
    private String code;
    private String description;

    PaymentTransferOption(String description, String code) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
