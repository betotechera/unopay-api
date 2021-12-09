package br.com.unopay.api.billing.remittance.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentOperationType  implements DescriptableEnum {

    CREDIT("C","LANÇAMENTO A CREDITO"),
    DEBIT("D","LANÇAMENTO A DEBITO"),
    EXTRACT_FOR_CONCILIATION("E","EXTRATO PARA CONCILIACAO"),
    EXTRACT_FOR_CASH_MANAGEMENT("G","EXTRATO PARA GESTAO DE CAIXA"),
    CAPTION_INFORMATION_FROM_OWN_BANK("I","INFORMAÇÕES DE TITULOS CAPTURADOS DO PROPRIO BANCO"),
    REMITTANCE_FILE("R","ARQUIVO REMESSA"),
    REMITTANCE_FILE_RETURN("T","ARQUIVO RETORNO");

    private String code;
    private String description;

    PaymentOperationType(String code, String description) {
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
