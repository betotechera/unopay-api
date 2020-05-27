package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.Product;
import lombok.Data;

@Data
public class ContractorProduct {
    private Contractor contractor;
    private Product product;

    public ContractorProduct(Contractor contractor, Product product) {
        if (contractor == null) throw new AssertionError();
        if (product == null) throw new AssertionError();
        this.contractor = contractor;
        this.product = product;
    }

    public String getDocumentNumber() {
        return contractor.getDocumentNumber();
    }

    public String getHirerDocument() {
        return contractor.getHirerDocument();
    }

    public String getIssuerDocument() {
        return contractor.getIssuerDocument();
    }
}
