package br.com.unopay.api.billing.remittance.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum PaymentServiceType implements DescriptableEnum {

    COLLECTION("01", "Cobrança"),
    BOLETO("03", "Bloqueto Eletrônico"),
    BANK_RECONCILIATION("04", "Conciliação Bancária"),
    DEBITS("05", "Débitos"),
    CHECK_CUSTODY("06", "Custódia de Cheques"),
    CASH_MANAGEMENT("07", "Gestão de Caixa"),
    CONSULTATION_MARGIN("08", "Consulta/Informação Margem"),
    CONSIGNMENT_VERIFICATION("09", "Averbação da Consignação"),
    PAYMENT_DIVIDENDS("10", "Pagamento Dividendos"),
    MAINTENANCE_CONSIGNMENT("11", "Manutenção da Consignação"),
    CONSIGNMENT_PLOTS("12", "Consignação de Parcelas"),
    GLOSSARY_CONSIGNATION("13", "Glosa da Consignação (INSS)"),
    CONSULTATION_OF_TAXES_PAYABLE("14", "Consulta de Tributos a pagar"),
    SUPPLIER_PAYMENT("20", "Pagamento a Fornecedor");

    private String code;
    private String description;

    PaymentServiceType(String code, String description) {
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
