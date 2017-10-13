package br.com.unopay.api.billing.creditcard.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;
import java.util.List;
import lombok.Data;

@Data
public class TransactionFilter {

    @SearchableField
    private List<String> orderId;
}
