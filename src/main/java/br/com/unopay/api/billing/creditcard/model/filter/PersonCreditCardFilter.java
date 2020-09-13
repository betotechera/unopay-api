package br.com.unopay.api.billing.creditcard.model.filter;

import br.com.unopay.api.billing.creditcard.model.CardBrand;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PersonCreditCardFilter implements Serializable{

    public static final long serialVersionUID = 1L;

    public PersonCreditCardFilter(){}

    @SearchableField(field = "person.id")
    private String person;

    @SearchableField
    private String lastFourDigits;

    @SearchableField
    private CardBrand brand;
}
