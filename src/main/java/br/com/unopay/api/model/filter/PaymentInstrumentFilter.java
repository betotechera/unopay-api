package br.com.unopay.api.model.filter;

import br.com.unopay.api.model.PaymentInstrumentSituation;
import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PaymentInstrumentFilter implements Serializable{

    @SearchableField
    private String number;

    @SearchableField(field = "product.code")
    private String productCode;

    @SearchableField(field = "hirer.id")
    private String hirer;

    @SearchableField
    private PaymentInstrumentSituation situation;


}
