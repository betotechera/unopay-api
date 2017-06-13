package br.com.unopay.api.model.filter;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.model.CreditSituation;
import br.com.unopay.api.model.Period;
import br.com.unopay.api.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class ContractorInstrumentCreditFilter  implements Serializable {

    public static final long serialVersionUID = 1L;

    ContractorInstrumentCreditFilter(){}

    @SearchableField(field = "createdDateTime")
    private Period createdDateTimePeriod;

    @SearchableField
    private CreditSituation situation;

    @SearchableField(field = "contract.id")
    private String contract;

    @SearchableField(field = "serviceType")
    private ServiceType serviceType;

    @SearchableField(field = "paymentInstrument.id")
    private String paymentInstrument;

    @SearchableField(field = "expirationDateTime")
    private Period expirationDateTimePeriod;

}
