package br.com.unopay.api.billing.creditcard.model.filter;

import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.model.Period;
import br.com.unopay.api.repository.filter.SearchableField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class TransactionFilter implements Serializable {

    public static final Long serialVersionUID = 1L;


    @SearchableField
    private List<String> orderId;

    @SearchableField
    private Period createDateTime;

    @SearchableField
    private TransactionStatus status;


    public List<String> getOrderId(){
        Optional<List<String>> list = Optional.ofNullable(this.orderId);
        return list.orElse(new ArrayList<>());
    }
}
