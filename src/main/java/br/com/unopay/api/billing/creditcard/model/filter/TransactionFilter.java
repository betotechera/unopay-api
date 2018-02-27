package br.com.unopay.api.billing.creditcard.model.filter;

import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Data;

@Data
public class TransactionFilter implements Serializable {

    public static final Long serialVersionUID = 1L;


    @SearchableField
    private Set<String> orderId;

    @SearchableField
    private Period createDateTime;

    @SearchableField
    private TransactionStatus status;


    public Set<String> getOrderId(){
        Optional<Set<String>> list = Optional.ofNullable(this.orderId);
        return list.orElse(new HashSet<>());
    }
}
