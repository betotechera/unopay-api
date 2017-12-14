package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.PaymentInstrumentSituation;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentInstrumentFilter  implements Serializable{

    public static final long serialVersionUID = 1L;

    PaymentInstrumentFilter(){}

    @SearchableField
    private String number;

    @SearchableField(field = "product.code")
    private String productCode;

    @SearchableField(field = "contractor.id")
    private String contractor;

    @SearchableField
    private PaymentInstrumentSituation situation;

    @SearchableField(field = "contractor.person.document.number")
    private String contractorDocumentNumber;

    @SearchableField(field = "product.issuer.id")
    private String issuer;


}
