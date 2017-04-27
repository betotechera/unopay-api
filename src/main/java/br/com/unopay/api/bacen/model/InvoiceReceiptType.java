package br.com.unopay.api.bacen.model;

import br.com.unopay.api.filter.DescriptibleEnum;

public enum InvoiceReceiptType implements DescriptibleEnum {
    XML("xml"), MANUAL("manual");

    private String description;

    InvoiceReceiptType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
