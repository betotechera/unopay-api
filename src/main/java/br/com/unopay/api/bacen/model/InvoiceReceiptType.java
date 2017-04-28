package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptionEnum;

public enum InvoiceReceiptType implements DescriptionEnum {
    XML("xml"), MANUAL("manual");

    private String description;

    InvoiceReceiptType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
