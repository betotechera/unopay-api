package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum IssueInvoiceType implements DescriptableEnum {

    BY_AUTHORIZATION("Nota fiscal por autorizaçao", "1"), BY_BATCH("Nota fiscal por lote", "2");

    private String description;

    public String getCode() {
        return code;
    }

    private String code;

    IssueInvoiceType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
}
