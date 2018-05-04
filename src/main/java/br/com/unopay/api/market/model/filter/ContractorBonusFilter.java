package br.com.unopay.api.market.model.filter;

import br.com.unopay.api.market.model.BonusSituation;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ContractorBonusFilter implements Serializable {
    public ContractorBonusFilter(){}

    @SearchableField(field = "product.name")
    private String product;

    @SearchableField(field = "payer.document.number")
    private String payer;

    @SearchableField
    private BonusSituation situation;
}
