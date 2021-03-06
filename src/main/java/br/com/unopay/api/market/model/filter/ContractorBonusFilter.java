package br.com.unopay.api.market.model.filter;

import br.com.unopay.api.market.model.BonusSituation;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ContractorBonusFilter implements Serializable {
    public ContractorBonusFilter(){}

    @SearchableField(field = "contractor.id")
    private String contractor;

    @SearchableField(field = "product.code")
    private String product;

    @SearchableField(field = "payer.document.number")
    private String payer;

    @SearchableField
    private BonusSituation situation;

    @SearchableField(field = "product.issuer.id")
    private String issuer;

    @SearchableField
    private String sourceIdentification;
}
