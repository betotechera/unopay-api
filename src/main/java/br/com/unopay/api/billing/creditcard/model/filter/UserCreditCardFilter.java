package br.com.unopay.api.billing.creditcard.model.filter;

import br.com.unopay.api.billing.creditcard.model.CardBrand;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class UserCreditCardFilter implements Serializable{

    public static final long serialVersionUID = 1L;

    UserCreditCardFilter(){}

    @SearchableField(field = "user.id")
    private String user;

    @SearchableField
    private String lastFourDigits;

    @SearchableField
    private CardBrand brand;
}
